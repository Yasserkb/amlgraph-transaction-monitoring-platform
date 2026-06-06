package com.amlgraph.graph.service;

import com.amlgraph.graph.api.GraphModels.GraphEdge;
import com.amlgraph.graph.api.GraphModels.GraphNode;
import com.amlgraph.graph.api.GraphModels.TransactionGraphResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GraphAnalysisService {
    public TransactionGraphResponse relatedGraph(UUID transactionId) {
        // Portfolio-ready mock implementation. Replace with Neo4j Cypher queries for production.
        var source = new GraphNode("account-a", "Source Account", "ACCOUNT", "MEDIUM");
        var destination = new GraphNode("account-b", "Destination Account", "ACCOUNT", "HIGH");
        var customer = new GraphNode("customer-1", "Customer", "CUSTOMER", "HIGH");
        var country = new GraphNode("country-ae", "AE", "COUNTRY", "HIGH");
        var sent = new GraphEdge("edge-" + transactionId, source.id(), destination.id(), "SENT", "15000 EUR");
        var owns = new GraphEdge("owns-1", customer.id(), source.id(), "OWNS", "");
        var located = new GraphEdge("located-1", destination.id(), country.id(), "LOCATED_IN", "");
        return new TransactionGraphResponse(List.of(customer, source, destination, country), List.of(owns, sent, located));
    }
}
