package com.amlgraph.transaction.api.dto;

import com.amlgraph.common.domain.TransactionStatus;
import com.amlgraph.transaction.domain.Channel;
import com.amlgraph.transaction.domain.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID customerId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        String currency,
        TransactionType transactionType,
        Channel channel,
        TransactionStatus status,
        String originCountry,
        String destinationCountry,
        String reference,
        Instant executedAt,
        Instant createdAt
) {}
