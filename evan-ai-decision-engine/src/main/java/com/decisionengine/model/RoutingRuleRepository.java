package com.decisionengine.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;
import java.util.List;

public interface RoutingRuleRepository extends JpaRepository<RoutingRule, Long> {
    Page<RoutingRule> findByNameContaining(String name, Pageable pageable);
    List<RoutingRule> findByEnabledTrueOrderByPriorityDesc();
}