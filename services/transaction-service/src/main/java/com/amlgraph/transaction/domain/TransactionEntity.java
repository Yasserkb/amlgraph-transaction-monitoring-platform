package com.amlgraph.transaction.domain;

import com.amlgraph.common.domain.TransactionStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transactions_customer", columnList = "customer_id"),
        @Index(name = "idx_transactions_executed_at", columnList = "executed_at"),
        @Index(name = "idx_transactions_status", columnList = "status")
})
public class TransactionEntity {
    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "source_account_id", nullable = false)
    private UUID sourceAccountId;

    @Column(name = "destination_account_id", nullable = false)
    private UUID destinationAccountId;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(name = "origin_country", length = 2)
    private String originCountry;

    @Column(name = "destination_country", length = 2)
    private String destinationCountry;

    @Column(length = 255)
    private String reference;

    @Column(name = "executed_at", nullable = false)
    private Instant executedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected TransactionEntity() {}

    public TransactionEntity(UUID id, UUID customerId, UUID sourceAccountId, UUID destinationAccountId,
                             BigDecimal amount, String currency, TransactionType transactionType, Channel channel,
                             TransactionStatus status, String originCountry, String destinationCountry,
                             String reference, Instant executedAt, Instant createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.channel = channel;
        this.status = status;
        this.originCountry = originCountry;
        this.destinationCountry = destinationCountry;
        this.reference = reference;
        this.executedAt = executedAt;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public UUID getSourceAccountId() { return sourceAccountId; }
    public UUID getDestinationAccountId() { return destinationAccountId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public TransactionType getTransactionType() { return transactionType; }
    public Channel getChannel() { return channel; }
    public TransactionStatus getStatus() { return status; }
    public String getOriginCountry() { return originCountry; }
    public String getDestinationCountry() { return destinationCountry; }
    public String getReference() { return reference; }
    public Instant getExecutedAt() { return executedAt; }
    public Instant getCreatedAt() { return createdAt; }
}
