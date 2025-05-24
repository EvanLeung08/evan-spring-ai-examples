package com.decisionengine.service;

import com.decisionengine.model.RoutingRule;
import com.decisionengine.model.RoutingRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DecisionEngineService {
    private final RoutingRuleRepository ruleRepo;
    private final List<ToolExecutor> executors;
    private final AIDecisionService aiDecisionService;

    @Cacheable(value = "activeRules", unless = "#result == null", cacheManager = "cacheManager")
    public List<RoutingRule> getActiveRules() {
        return ruleRepo.findByEnabledTrueOrderByPriorityDesc();
    }

    public Object routeAndExecute(String userRequest) {
        List<RoutingRule> rules = getActiveRules();
        for (RoutingRule rule : rules) {
            if ("KEYWORD".equals(rule.getCondition())) {
                for (String kw : rule.getExpression().split("\\|")) {
                    if (userRequest.contains(kw)) {
                        return executeTool(rule, userRequest);
                    }
                }
            } else if ("REGEX".equals(rule.getCondition())) {
                if (Pattern.compile(rule.getExpression()).matcher(userRequest).find()) {
                    return executeTool(rule, userRequest);
                }
            }
        }
        // AI 语义匹配
        List<RoutingRule> aiRules = rules.stream()
                .filter(r -> "AI".equals(r.getCondition()))
                .toList();
        if (!aiRules.isEmpty()) {
            List<String> tools = aiRules.stream().map(RoutingRule::getToolType).toList();
            AIDecisionService.ToolDecision decision = aiDecisionService.makeDecision(userRequest, tools);
            if (decision.tool() != null) {
                RoutingRule rule = aiRules.stream()
                        .filter(r -> r.getToolType().equals(decision.tool()))
                        .findFirst().orElse(null);
                if (rule != null) {
                    return executeTool(rule, userRequest);
                }
            }
        }
        throw new RuleNotFoundException("No matching rule found");
    }

    private Object executeTool(RoutingRule rule, String userRequest) {
        return executors.stream()
                .filter(e -> e.getToolType().equals(rule.getToolType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No executor found"))
                .execute(userRequest, rule.getToolConfig(), rule.getToolScript());
    }
}