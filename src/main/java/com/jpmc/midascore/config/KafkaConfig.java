src\main\java\com\jpmc\midascore\config\KafkaConfig.java
package com.jpmc.midascore.config;

import com.jpmc.midascore.foundation.Transaction;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    // ---- PRODUCER ----
    @SuppressWarnings("deprecation") // OK for Spring Boot 3.2.x
    @Bean
    public ProducerFactory<String, Transaction> transactionProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties();
        // Make sure values are JSON (no type headers; consumer sets default type)
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                new JsonSerializer<>()
        );
    }

    @Bean
    public KafkaTemplate<String, Transaction> kafkaTemplate(
            ProducerFactory<String, Transaction> transactionProducerFactory
    ) {
        return new KafkaTemplate<>(transactionProducerFactory);
    }

    // ---- CONSUMER ----
    @SuppressWarnings("deprecation") // OK for Spring Boot 3.2.x
    @Bean
    public ConsumerFactory<String, Transaction> transactionConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();

        // Trust package(s) + default target type = Transaction
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Transaction.class.getName());

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(Transaction.class, false) // disable type headers
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Transaction> kafkaListenerContainerFactory(
            ConsumerFactory<String, Transaction> transactionConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Transaction> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(transactionConsumerFactory);
        return factory;
    }
}


src\main\java\com\jpmc\midascore\entity\TransactionRecord.java
package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many transactions can reference the same user
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private UserRecord sender;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private UserRecord recipient;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private float incentive = 0.0f;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected TransactionRecord() {}

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }
     public float getIncentive() { return incentive; }
    public void setIncentive(float incentive) { this.incentive = incentive; }
    public Long getId() { return id; }
    public UserRecord getSender() { return sender; }
    public UserRecord getRecipient() { return recipient; }
    public float getAmount() { return amount; }
    public Instant getCreatedAt() { return createdAt; }
}


src\main\java\com\jpmc\midascore\entity\UserRecord.java
package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
public class UserRecord {

    @Id
    @GeneratedValue()
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private float balance;

    protected UserRecord() {
    }

    public UserRecord(String name, float balance) {
        this.name = name;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, name='%s', balance='%f'", id, name, balance);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
}
