package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
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

    public TransactionService(UserRepository userRepository,
                              TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Validates and applies a transaction atomically.
     * Rules:
     *  - sender exists
     *  - recipient exists
     *  - sender.balance >= amount
     *  If valid: persist TransactionRecord and update both balances.
     */
    @Transactional
    public void process(Transaction tx) {
        long senderId = tx.getSenderId();
        long recipientId = tx.getRecipientId();
        float amount = tx.getAmount();

        UserRecord sender = userRepository.findById(senderId);
        UserRecord recipient = userRepository.findById(recipientId);

        if (sender == null || recipient == null) {
            log.debug("Discarding tx: invalid user(s). senderId={}, recipientId={}", senderId, recipientId);
            return; // invalid
        }

        if (sender.getBalance() < amount) {
            log.debug("Discarding tx: insufficient funds. senderId={}, balance={}, amount={}",
                    senderId, sender.getBalance(), amount);
            return; // invalid
        }

        // apply balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        // persist updates + transaction record
        userRepository.save(sender);
        userRepository.save(recipient);
        transactionRepository.save(new TransactionRecord(sender, recipient, amount));

        log.debug("Applied tx OK: {} -> {} amount={}", senderId, recipientId, amount);
    }
}
