package com.amlgraph.ruleengine.api;

import com.amlgraph.common.api.ApiResponse;
import com.amlgraph.ruleengine.domain.RuleDefinition;
import com.amlgraph.ruleengine.service.AmlRuleEngine;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
public class RuleController {
    private final AmlRuleEngine ruleEngine;

    public RuleController(AmlRuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    @GetMapping
    public ApiResponse<List<RuleDefinition>> rules() {
        return ApiResponse.of(ruleEngine.rules());
    }
}
