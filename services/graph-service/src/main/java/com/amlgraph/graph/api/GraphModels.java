package com.amlgraph.graph.api;

import java.util.List;

public final class GraphModels {
    private GraphModels() {}

    public record GraphNode(String id, String label, String type, String riskLevel) {}
    public record GraphEdge(String id, String source, String target, String label, String amount) {}
    public record TransactionGraphResponse(List<GraphNode> nodes, List<GraphEdge> edges) {}
}
