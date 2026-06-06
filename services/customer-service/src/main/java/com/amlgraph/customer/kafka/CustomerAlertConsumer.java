package com.amlgraph.customer.kafka;

import com.amlgraph.common.event.AlertCreatedEvent;
import com.amlgraph.common.json.Jsons;
import com.amlgraph.customer.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerAlertConsumer {
    private static final Logger log = LoggerFactory.getLogger(CustomerAlertConsumer.class);
    private final CustomerService customerService;

    public CustomerAlertConsumer(CustomerService customerService) {
        this.customerService = customerService;
    }

    @KafkaListener(topics = "alerts.created", groupId = "customer-service")
    public void onAlertCreated(String eventJson) {
        var event = Jsons.fromEvent(eventJson, AlertCreatedEvent.class).payload();
        customerService.applyAlertImpact(event);
        log.info("Applied risk impact for customerId={} severity={}", event.customerId(), event.severity());
    }
}
