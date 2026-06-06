package com.amlgraph.graph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.amlgraph.graph", "com.amlgraph.common"})
public class GraphServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphServiceApplication.class, args);
    }
}
