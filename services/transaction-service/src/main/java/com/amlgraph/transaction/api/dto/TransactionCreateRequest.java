package com.amlgraph.transaction.api.dto;

import com.amlgraph.transaction.domain.Channel;
import com.amlgraph.transaction.domain.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionCreateRequest(
        @NotNull UUID customerId,
        @NotNull UUID sourceAccountId,
        @NotNull UUID destinationAccountId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotBlank @Size(min = 3, max = 3) String currency,
        @NotNull TransactionType transactionType,
        @NotNull Channel channel,
        @Size(min = 2, max = 2) String originCountry,
        @Size(min = 2, max = 2) String destinationCountry,
        @Size(max = 255) String reference,
        @NotNull Instant executedAt
) {}
