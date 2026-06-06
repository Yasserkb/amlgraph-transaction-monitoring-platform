package com.amlgraph.graph.service;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GraphAnalysisServiceTest {
    @Test
    void relatedGraph_shouldReturnNodesAndEdges() {
        var result = new GraphAnalysisService().relatedGraph(UUID.randomUUID());

        assertThat(result.nodes()).hasSizeGreaterThanOrEqualTo(3);
        assertThat(result.edges()).extracting("label").contains("SENT", "OWNS");
    }
}
