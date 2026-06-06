package com.amlgraph.graph.api;

import com.amlgraph.common.api.ApiResponse;
import com.amlgraph.graph.api.GraphModels.TransactionGraphResponse;
import com.amlgraph.graph.service.GraphAnalysisService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/graph")
public class GraphController {
    private final GraphAnalysisService service;

    public GraphController(GraphAnalysisService service) {
        this.service = service;
    }

    @GetMapping("/transactions/{transactionId}")
    public ApiResponse<TransactionGraphResponse> related(@PathVariable UUID transactionId) {
        return ApiResponse.of(service.relatedGraph(transactionId));
    }
}
