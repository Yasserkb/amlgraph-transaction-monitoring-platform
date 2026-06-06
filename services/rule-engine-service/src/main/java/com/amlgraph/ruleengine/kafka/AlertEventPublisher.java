package com.amlgraph.ruleengine.kafka;

import com.amlgraph.common.event.AlertCreatedEvent;
import com.amlgraph.common.event.EventEnvelope;
import com.amlgraph.common.event.TransactionCreatedEvent;
import com.amlgraph.common.json.Jsons;
import com.amlgraph.ruleengine.domain.RuleResult;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class AlertEventPublisher {
    public static final String ALERTS_CREATED_TOPIC = "alerts.created";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public AlertEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(TransactionCreatedEvent transaction, RuleResult result) {
        var alertId = UUID.randomUUID();
        var payload = new AlertCreatedEvent(
                alertId,
                transaction.transactionId(),
                transaction.customerId(),
                result.ruleId(),
                result.severity(),
                result.description(),
                Instant.now()
        );
        kafkaTemplate.send(ALERTS_CREATED_TOPIC, alertId.toString(), Jsons.toJson(EventEnvelope.of("ALERT_CREATED", payload)));
    }
}
