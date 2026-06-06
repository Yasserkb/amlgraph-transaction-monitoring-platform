package com.amlgraph.alert.domain;

import com.amlgraph.common.domain.AlertStatus;
import com.amlgraph.common.domain.Severity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts")
public class AlertEntity {
    @Id
    private UUID id;
    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;
    @Column(name = "rule_id", nullable = false)
    private String ruleId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status;
    @Column(nullable = false, length = 1000)
    private String description;
    @Column(name = "triggered_at", nullable = false)
    private Instant triggeredAt;
    @Column(name = "closed_at")
    private Instant closedAt;

    protected AlertEntity() {}

    public AlertEntity(UUID id, UUID transactionId, UUID customerId, String ruleId, Severity severity,
                       AlertStatus status, String description, Instant triggeredAt, Instant closedAt) {
        this.id = id;
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.ruleId = ruleId;
        this.severity = severity;
        this.status = status;
        this.description = description;
        this.triggeredAt = triggeredAt;
        this.closedAt = closedAt;
    }

    public void updateStatus(AlertStatus status) {
        this.status = status;
        if (status == AlertStatus.CLOSED || status == AlertStatus.FALSE_POSITIVE) {
            this.closedAt = Instant.now();
        }
    }

    public UUID getId() { return id; }
    public UUID getTransactionId() { return transactionId; }
    public UUID getCustomerId() { return customerId; }
    public String getRuleId() { return ruleId; }
    public Severity getSeverity() { return severity; }
    public AlertStatus getStatus() { return status; }
    public String getDescription() { return description; }
    public Instant getTriggeredAt() { return triggeredAt; }
    public Instant getClosedAt() { return closedAt; }
}
