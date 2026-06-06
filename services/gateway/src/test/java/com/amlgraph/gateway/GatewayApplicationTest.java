package com.amlgraph.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "amlgraph.security.enabled=false")
class GatewayApplicationTest {
    @Test
    void contextLoads() {}
}
