package com.decisionengine.controller;

import com.decisionengine.model.*;
import com.decisionengine.service.DecisionEngineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleController {
    private final RoutingRuleRepository ruleRepo;
    private final DecisionEngineService decisionEngineService;

    @GetMapping
    public Page<RoutingRule> list(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ruleRepo.findByNameContaining(search, PageRequest.of(page, size, Sort.by("priority").descending()));
    }

    @PostMapping
    public RoutingRule create(@Valid @RequestBody RoutingRule rule) {
        return ruleRepo.save(rule);
    }

    @GetMapping("/{id}")
    public RoutingRule getById(@PathVariable Long id) {
        return ruleRepo.findById(id).orElseThrow(() -> new RuntimeException("Rule not found"));
    }

    @PutMapping("/{id}")
    public RoutingRule update(@PathVariable Long id, @Valid @RequestBody RoutingRule rule) {
        rule.setId(id);
        return ruleRepo.save(rule);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ruleRepo.deleteById(id);
    }

    @PostMapping("/execute")
    public Object executeRule(@RequestBody Map<String, String> req) {
        String userRequest = req.get("request");
        return decisionEngineService.routeAndExecute(userRequest);
    }
}