package com.decisionengine.service;

import com.decisionengine.model.*;
import com.decisionengine.repository.WorkflowNodeRepository;
import com.decisionengine.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowExecutionService {
    private final WorkflowRepository workflowRepo;
    private final WorkflowNodeRepository nodeRepo;
    private final List<ToolExecutor> executors;
    private final ScriptToolExecutor scriptExecutor;

    public Object executeWorkflow(Long workflowId, String userRequest) {
        Workflow workflow = workflowRepo.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found: " + workflowId));
        
        if (!workflow.getEnabled()) {
            throw new RuntimeException("Workflow is disabled: " + workflow.getName());
        }
        
        List<WorkflowNode> nodes = nodeRepo.findByWorkflowIdOrderByPosition(workflowId);
        if (nodes.isEmpty()) {
            throw new RuntimeException("Workflow has no nodes: " + workflow.getName());
        }
        
        // Start with user request as initial input
        Object currentInput = userRequest;
        
        // Execute each node in sequence, passing results forward
        for (WorkflowNode node : nodes) {
            log.info("Executing node: {} in workflow: {}", node.getName(), workflow.getName());
            
            // Execute the current node with the input from previous node
            Object nodeResult = executeNode(node, currentInput);
            
            // Pass this node's output as input to the next node
            currentInput = nodeResult;
            
            log.info("Node execution complete: {}, result type: {}", 
                     node.getName(), nodeResult != null ? nodeResult.getClass().getName() : "null");
        }
        
        // Return the result of the final node
        return currentInput;
    }
    
    private Object executeNode(WorkflowNode node, Object input) {
        // Convert input to string if needed
        String inputStr = input instanceof String ? (String) input : String.valueOf(input);
        
        // Find appropriate executor based on tool type
        ToolExecutor executor = executors.stream()
                .filter(e -> e.getToolType().equals(node.getToolType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No executor found for tool type: " + node.getToolType()));
        
        // Execute the node using the executor
        return executor.execute(inputStr, node.getToolConfig(), node.getToolScript());
    }
}