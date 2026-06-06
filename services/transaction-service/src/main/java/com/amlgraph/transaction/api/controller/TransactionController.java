package com.amlgraph.transaction.api.controller;

import com.amlgraph.common.api.ApiResponse;
import com.amlgraph.common.api.PagedResponse;
import com.amlgraph.common.domain.TransactionStatus;
import com.amlgraph.transaction.api.dto.TransactionCreateRequest;
import com.amlgraph.transaction.api.dto.TransactionResponse;
import com.amlgraph.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ApiResponse<TransactionResponse> create(@Valid @RequestBody TransactionCreateRequest request) {
        return ApiResponse.of(transactionService.create(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<TransactionResponse> get(@PathVariable UUID id) {
        return ApiResponse.of(transactionService.get(id));
    }

    @GetMapping
    public PagedResponse<TransactionResponse> search(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return transactionService.search(customerId, status, page, size);
    }
}
