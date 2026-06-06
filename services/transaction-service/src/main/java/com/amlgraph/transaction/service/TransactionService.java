package com.amlgraph.transaction.service;

import com.amlgraph.common.api.PagedResponse;
import com.amlgraph.common.domain.TransactionStatus;
import com.amlgraph.common.exception.NotFoundException;
import com.amlgraph.transaction.api.dto.TransactionCreateRequest;
import com.amlgraph.transaction.api.dto.TransactionResponse;
import com.amlgraph.transaction.api.mapper.TransactionMapper;
import com.amlgraph.transaction.kafka.TransactionEventPublisher;
import com.amlgraph.transaction.repository.TransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionEventPublisher eventPublisher;

    public TransactionService(TransactionRepository transactionRepository,
                              TransactionMapper transactionMapper,
                              TransactionEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public TransactionResponse create(TransactionCreateRequest request) {
        var entity = transactionMapper.toEntity(request);
        var saved = transactionRepository.save(entity);
        publishAfterCommit(saved);
        return transactionMapper.toResponse(saved);
    }

    private void publishAfterCommit(com.amlgraph.transaction.domain.TransactionEntity saved) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    eventPublisher.publishCreated(saved);
                }
            });
            return;
        }
        eventPublisher.publishCreated(saved);
    }

    @Transactional(readOnly = true)
    public TransactionResponse get(UUID id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Transaction not found: " + id));
    }

    @Transactional(readOnly = true)
    public PagedResponse<TransactionResponse> search(UUID customerId, TransactionStatus status, int page, int size) {
        var result = transactionRepository.search(customerId, status, PageRequest.of(page, size));
        var data = result.getContent().stream().map(transactionMapper::toResponse).toList();
        return PagedResponse.of(data, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }
}
