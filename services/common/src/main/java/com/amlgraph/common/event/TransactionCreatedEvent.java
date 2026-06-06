package com.amlgraph.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionCreatedEvent(
        UUID transactionId,
        UUID customerId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        String currency,
        String transactionType,
        String channel,
        String originCountry,
        String destinationCountry,
        String reference,
        Instant executedAt
) {}
