package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.integration.IncentiveClient;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IncentiveClient incentiveClient;

    public TransactionService(UserRepository userRepository,
                              TransactionRepository transactionRepository,
                              IncentiveClient incentiveClient) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveClient = incentiveClient;
    }

    @Transactional
    public void process(Transaction tx) {
        long senderId = tx.getSenderId();
        long recipientId = tx.getRecipientId();
        float amount = tx.getAmount();

        UserRecord sender = userRepository.findById(senderId);
        UserRecord recipient = userRepository.findById(recipientId);

        if (!isValid(sender, recipient, amount)) {
            log.debug("Discarding tx: invalid. senderId={}, recipientId={}, amount={}, senderBalance={}",
                    senderId, recipientId, amount, sender != null ? sender.getBalance() : null);
            return;
        }

        try {
            // 1) Ask Incentive API
            Incentive incentive = incentiveClient.fetch(new Transaction(sender.getId(), recipient.getId(), amount));
            float inc = incentive != null ? Math.max(0f, incentive.getAmount()) : 0f;

            // 2) Persist TransactionRecord with incentive
            TransactionRecord rec = new TransactionRecord(sender, recipient, amount);
            rec.setIncentive(inc); // assumes your entity has this setter
            transactionRepository.save(rec);

            // 3) Update balances
            sender.setBalance(sender.getBalance() - amount);
            recipient.setBalance(recipient.getBalance() + amount + inc);

            userRepository.save(sender);
            userRepository.save(recipient);

            log.debug("Applied tx OK: {} -> {} amount={} incentive={}", senderId, recipientId, amount, inc);
        } catch (Exception e) {
            // If incentive call fails, process with 0 incentive
            log.warn("Incentive API call failed; processing without incentive. cause={}", e.getMessage());

            float inc = 0f;
            TransactionRecord rec = new TransactionRecord(sender, recipient, amount);
            rec.setIncentive(inc);
            transactionRepository.save(rec);

            sender.setBalance(sender.getBalance() - amount);
            recipient.setBalance(recipient.getBalance() + amount);

            userRepository.save(sender);
            userRepository.save(recipient);

            log.debug("Applied tx without incentive: {} -> {} amount={}", senderId, recipientId, amount);
        }
    }

    private boolean isValid(UserRecord sender, UserRecord recipient, float amount) {
        if (sender == null || recipient == null) return false;
        if (amount < 0) return false;
        return sender.getBalance() >= amount;
    }
}
