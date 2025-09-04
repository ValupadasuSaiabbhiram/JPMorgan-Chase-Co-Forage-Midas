package com.jpmc.midascore.messaging;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionListener.class);

    // Listens to topic configured as general.kafka-topic (from application.yml)
    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "midas-core",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(Transaction tx) {
        // For Task 2 you only need to receive them; logging helps you see flow if not debugging
        log.info("Received transaction: {}", tx);
        // No further processing yet (later tasks will use this).
    }
}
