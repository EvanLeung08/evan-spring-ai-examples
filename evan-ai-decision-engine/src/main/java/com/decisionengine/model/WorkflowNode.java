package com.decisionengine.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    private Integer position;

    @NotBlank
    private String toolType;  // MCP/API

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String toolConfig;  // JSON

    @Column(columnDefinition = "TEXT")
    private String toolScript;  // Groovy script for node execution

    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    @JsonBackReference  // Breaks the circular reference in JSON serialization
    private Workflow workflow;
}