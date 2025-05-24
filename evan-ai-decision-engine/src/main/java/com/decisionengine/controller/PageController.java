package com.decisionengine.controller;

import com.decisionengine.model.RoutingRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final RoutingRuleRepository ruleRepo;

    @GetMapping("/rules")
    public String rulesPage(Model model) {
        model.addAttribute("rules", ruleRepo.findAll());
        return "rules";
    }
}