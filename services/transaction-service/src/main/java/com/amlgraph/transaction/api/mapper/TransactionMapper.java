package com.amlgraph.transaction.api.mapper;

import com.amlgraph.common.domain.TransactionStatus;
import com.amlgraph.transaction.api.dto.TransactionCreateRequest;
import com.amlgraph.transaction.api.dto.TransactionResponse;
import com.amlgraph.transaction.domain.TransactionEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class TransactionMapper {
    public TransactionEntity toEntity(TransactionCreateRequest request) {
        return new TransactionEntity(
                UUID.randomUUID(),
                request.customerId(),
                request.sourceAccountId(),
                request.destinationAccountId(),
                request.amount(),
                request.currency().toUpperCase(),
                request.transactionType(),
                request.channel(),
                TransactionStatus.COMPLETED,
                uppercaseOrNull(request.originCountry()),
                uppercaseOrNull(request.destinationCountry()),
                request.reference(),
                request.executedAt(),
                Instant.now()
        );
    }

    public TransactionResponse toResponse(TransactionEntity entity) {
        return new TransactionResponse(
                entity.getId(),
                entity.getCustomerId(),
                entity.getSourceAccountId(),
                entity.getDestinationAccountId(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getTransactionType(),
                entity.getChannel(),
                entity.getStatus(),
                entity.getOriginCountry(),
                entity.getDestinationCountry(),
                entity.getReference(),
                entity.getExecutedAt(),
                entity.getCreatedAt()
        );
    }

    private String uppercaseOrNull(String value) {
        return value == null ? null : value.toUpperCase();
    }
}
