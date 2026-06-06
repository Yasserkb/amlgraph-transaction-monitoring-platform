package com.amlgraph.alert.api.controller;

import com.amlgraph.alert.api.dto.*;
import com.amlgraph.alert.service.AlertCaseService;
import com.amlgraph.common.api.ApiResponse;
import com.amlgraph.common.api.PagedResponse;
import com.amlgraph.common.domain.CaseStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cases")
public class CaseController {
    private final AlertCaseService service;

    public CaseController(AlertCaseService service) {
        this.service = service;
    }

    @GetMapping
    public PagedResponse<CaseResponse> search(@RequestParam(required = false) CaseStatus status,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        return service.searchCases(status, page, size);
    }

    @GetMapping("/{id}")
    public ApiResponse<CaseResponse> get(@PathVariable UUID id) {
        return ApiResponse.of(service.getCase(id));
    }

    @PutMapping("/{id}/assign")
    public ApiResponse<CaseResponse> assign(@PathVariable UUID id, @Valid @RequestBody AssignCaseRequest request) {
        return ApiResponse.of(service.assignCase(id, request.analystId()));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<CaseResponse> status(@PathVariable UUID id, @Valid @RequestBody UpdateCaseStatusRequest request) {
        return ApiResponse.of(service.updateCaseStatus(id, request.status()));
    }

    @PostMapping("/{id}/notes")
    public ApiResponse<CaseResponse> note(@PathVariable UUID id, @Valid @RequestBody AddCaseNoteRequest request) {
        return ApiResponse.of(service.addCaseNote(id, request.authorId(), request.content()));
    }
}
