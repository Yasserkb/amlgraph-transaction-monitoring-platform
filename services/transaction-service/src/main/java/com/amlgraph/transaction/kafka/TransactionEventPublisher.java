package com.amlgraph.transaction.kafka;

import com.amlgraph.common.event.EventEnvelope;
import com.amlgraph.common.event.TransactionCreatedEvent;
import com.amlgraph.common.json.Jsons;
import com.amlgraph.transaction.domain.TransactionEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventPublisher {
    public static final String TRANSACTIONS_RAW_TOPIC = "transactions.raw";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public TransactionEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCreated(TransactionEntity transaction) {
        var payload = new TransactionCreatedEvent(
                transaction.getId(),
                transaction.getCustomerId(),
                transaction.getSourceAccountId(),
                transaction.getDestinationAccountId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getTransactionType().name(),
                transaction.getChannel().name(),
                transaction.getOriginCountry(),
                transaction.getDestinationCountry(),
                transaction.getReference(),
                transaction.getExecutedAt()
        );
        var envelope = EventEnvelope.of("TRANSACTION_CREATED", payload);
        kafkaTemplate.send(TRANSACTIONS_RAW_TOPIC, transaction.getId().toString(), Jsons.toJson(envelope));
    }
}
