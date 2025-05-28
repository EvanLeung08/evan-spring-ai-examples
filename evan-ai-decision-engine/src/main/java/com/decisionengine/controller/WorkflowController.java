package com.decisionengine.controller;

import com.decisionengine.model.Workflow;
import com.decisionengine.model.WorkflowNode;
import com.decisionengine.repository.WorkflowNodeRepository;
import com.decisionengine.repository.WorkflowRepository;
import com.decisionengine.service.WorkflowExecutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {
    private final WorkflowRepository workflowRepo;
    private final WorkflowNodeRepository nodeRepo;
    private final WorkflowExecutionService executionService;

    @GetMapping
    public List<Workflow> listWorkflows() {
        return workflowRepo.findAll();
    }

    @PostMapping
    public Workflow createWorkflow(@Valid @RequestBody Workflow workflow) {
        return workflowRepo.save(workflow);
    }

    @GetMapping("/{id}")
    public Workflow getWorkflow(@PathVariable Long id) {
        return workflowRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow not found"));
    }

    @PutMapping("/{id}")
    public Workflow updateWorkflow(@PathVariable Long id, @Valid @RequestBody Workflow workflow) {
        if (!workflowRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow not found");
        }
        workflow.setId(id);
        return workflowRepo.save(workflow);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable Long id) {
        if (!workflowRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow not found");
        }
        workflowRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{workflowId}/nodes")
    public List<WorkflowNode> getWorkflowNodes(@PathVariable Long workflowId) {
        if (!workflowRepo.existsById(workflowId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow not found");
        }
        return nodeRepo.findByWorkflowIdOrderByPosition(workflowId);
    }

    @PostMapping("/{workflowId}/nodes")
    public WorkflowNode addNode(@PathVariable Long workflowId, @Valid @RequestBody WorkflowNode node) {
        Workflow workflow = workflowRepo.findById(workflowId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow not found"));

        // Ensure node has valid default values for required fields
        if (node.getToolConfig() == null || node.getToolConfig().isBlank()) {
            node.setToolConfig("{}");
        }

        // Set the workflow relationship explicitly
        node.setWorkflow(workflow);

        // Log before saving to verify data
        System.out.println("Saving node: " + node.getName() + " for workflow: " + workflow.getName());

        return nodeRepo.save(node);
    }
    @PutMapping("/nodes/{nodeId}")
    public WorkflowNode updateNode(@PathVariable Long nodeId, @Valid @RequestBody WorkflowNode node) {
        WorkflowNode existingNode = nodeRepo.findById(nodeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Node not found"));

        // Keep the same workflow reference
        Workflow workflow = existingNode.getWorkflow();

        node.setId(nodeId);
        node.setWorkflow(workflow);
        return nodeRepo.save(node);
    }

    @DeleteMapping("/nodes/{nodeId}")
    public ResponseEntity<Void> deleteNode(@PathVariable Long nodeId) {
        if (!nodeRepo.existsById(nodeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Node not found");
        }
        nodeRepo.deleteById(nodeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{workflowId}/execute")
    public Object executeWorkflow(@PathVariable Long workflowId, @RequestBody Map<String, String> request) {
        String userRequest = request.get("request");
        return executionService.executeWorkflow(workflowId, userRequest);
    }
}