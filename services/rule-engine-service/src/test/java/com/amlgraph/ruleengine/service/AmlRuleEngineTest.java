package com.amlgraph.ruleengine.service;

import com.amlgraph.common.domain.Severity;
import com.amlgraph.common.event.TransactionCreatedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AmlRuleEngineTest {
    private final AmlRuleEngine engine = new AmlRuleEngine();

    @Test
    void evaluate_shouldTriggerLargeTransactionAndHighRiskCountry() {
        var event = new TransactionCreatedEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("15000.00"), "EUR", "TRANSFER", "SWIFT", "FR", "AE", "demo",
                Instant.parse("2026-06-05T12:00:00Z")
        );

        var results = engine.evaluate(event);

        assertThat(results).hasSize(2);
        assertThat(results).extracting("ruleId").contains("R001", "R004");
        assertThat(results).extracting("severity").contains(Severity.HIGH);
    }

    @Test
    void evaluate_shouldEscalateToCriticalForVeryLargeTransaction() {
        var event = new TransactionCreatedEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("75000.00"), "EUR", "TRANSFER", "ONLINE", "FR", "DE", "critical",
                Instant.parse("2026-06-05T12:00:00Z")
        );

        var results = engine.evaluate(event);

        assertThat(results).anySatisfy(result -> {
            assertThat(result.ruleId()).isEqualTo("R001");
            assertThat(result.severity()).isEqualTo(Severity.CRITICAL);
        });
    }

    @Test
    void evaluate_shouldTriggerUnusualHourForLargeNightTransaction() {
        var event = new TransactionCreatedEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("7000.00"), "EUR", "TRANSFER", "ONLINE", "FR", "DE", "night",
                Instant.parse("2026-06-05T03:15:00Z")
        );

        var results = engine.evaluate(event);

        assertThat(results).extracting("ruleId").contains("R010");
    }
}
