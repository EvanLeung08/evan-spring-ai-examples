package com.decisionengine.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String condition; // KEYWORD/REGEX/AI

    @NotBlank
    private String expression;

    @NotBlank
    private String toolType; // MCP/API

    @NotBlank
    private String toolConfig; // JSON

    @NotNull
    @Min(1)
    @Max(100)
    private Integer priority;

    @NotNull
    private Boolean enabled;
}