package com.amlgraph.alert.api.controller;

import com.amlgraph.alert.api.dto.*;
import com.amlgraph.alert.service.AlertCaseService;
import com.amlgraph.common.api.ApiResponse;
import com.amlgraph.common.api.PagedResponse;
import com.amlgraph.common.domain.AlertStatus;
import com.amlgraph.common.domain.Severity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertCaseService service;

    public AlertController(AlertCaseService service) {
        this.service = service;
    }

    @GetMapping
    public PagedResponse<AlertResponse> search(@RequestParam(required = false) Severity severity,
                                               @RequestParam(required = false) AlertStatus status,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        return service.searchAlerts(severity, status, page, size);
    }

    @GetMapping("/{id}")
    public ApiResponse<AlertResponse> get(@PathVariable UUID id) {
        return ApiResponse.of(service.getAlert(id));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<AlertResponse> updateStatus(@PathVariable UUID id,
                                                   @Valid @RequestBody UpdateAlertStatusRequest request) {
        return ApiResponse.of(service.updateAlertStatus(id, request.status()));
    }
}
