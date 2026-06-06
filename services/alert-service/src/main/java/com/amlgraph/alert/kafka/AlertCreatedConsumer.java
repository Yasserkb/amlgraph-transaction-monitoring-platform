package com.amlgraph.alert.kafka;

import com.amlgraph.alert.service.AlertCaseService;
import com.amlgraph.common.event.AlertCreatedEvent;
import com.amlgraph.common.json.Jsons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AlertCreatedConsumer {
    private static final Logger log = LoggerFactory.getLogger(AlertCreatedConsumer.class);
    private final AlertCaseService service;

    public AlertCreatedConsumer(AlertCaseService service) {
        this.service = service;
    }

    @KafkaListener(topics = "alerts.created", groupId = "alert-service")
    public void onAlertCreated(String eventJson) {
        var event = Jsons.fromEvent(eventJson, AlertCreatedEvent.class).payload();
        service.handleAlertCreated(event);
        log.info("Alert persisted alertId={} transactionId={} severity={}", event.alertId(), event.transactionId(), event.severity());
    }
}
