package com.decisionengine.repository;

import com.decisionengine.model.WorkflowNode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkflowNodeRepository extends JpaRepository<WorkflowNode, Long> {
    List<WorkflowNode> findByWorkflowIdOrderByPosition(Long workflowId);
}