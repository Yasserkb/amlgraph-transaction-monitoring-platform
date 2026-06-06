package com.amlgraph.transaction.service;

import com.amlgraph.common.domain.TransactionStatus;
import com.amlgraph.transaction.api.dto.TransactionCreateRequest;
import com.amlgraph.transaction.api.mapper.TransactionMapper;
import com.amlgraph.transaction.domain.Channel;
import com.amlgraph.transaction.domain.TransactionEntity;
import com.amlgraph.transaction.domain.TransactionType;
import com.amlgraph.transaction.kafka.TransactionEventPublisher;
import com.amlgraph.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock TransactionRepository repository;
    @Mock TransactionEventPublisher publisher;

    @Test
    void create_shouldPersistTransactionAndPublishKafkaEvent() {
        var mapper = new TransactionMapper();
        var service = new TransactionService(repository, mapper, publisher);
        var request = new TransactionCreateRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("15000.00"), "eur", TransactionType.TRANSFER, Channel.SWIFT,
                "fr", "ae", "test", Instant.parse("2026-06-05T12:00:00Z")
        );
        when(repository.save(any(TransactionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.currency()).isEqualTo("EUR");
        assertThat(response.status()).isEqualTo(TransactionStatus.COMPLETED);

        var captor = ArgumentCaptor.forClass(TransactionEntity.class);
        verify(repository).save(captor.capture());
        verify(publisher).publishCreated(captor.getValue());
        verifyNoMoreInteractions(publisher);
    }
}
