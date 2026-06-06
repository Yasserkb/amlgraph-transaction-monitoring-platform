package com.amlgraph.ruleengine.kafka;

import com.amlgraph.common.event.TransactionCreatedEvent;
import com.amlgraph.common.json.Jsons;
import com.amlgraph.ruleengine.service.AmlRuleEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {
    private static final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);

    private final AmlRuleEngine ruleEngine;
    private final AlertEventPublisher alertEventPublisher;

    public TransactionConsumer(AmlRuleEngine ruleEngine, AlertEventPublisher alertEventPublisher) {
        this.ruleEngine = ruleEngine;
        this.alertEventPublisher = alertEventPublisher;
    }

    @KafkaListener(topics = "transactions.raw", groupId = "rule-engine-service")
    public void onTransaction(String eventJson) {
        var envelope = Jsons.fromEvent(eventJson, TransactionCreatedEvent.class);
        var transaction = envelope.payload();
        var results = ruleEngine.evaluate(transaction);
        log.info("AML evaluation completed transactionId={} rulesFired={}", transaction.transactionId(), results.size());
        results.forEach(result -> alertEventPublisher.publish(transaction, result));
    }
}
