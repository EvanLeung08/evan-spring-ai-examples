package com.decisionengine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class McpToolExecutor implements ToolExecutor {
    private final ScriptToolExecutor scriptToolExecutor;

    @Override
    public String getToolType() { return "MCP"; }

    @Override
    public Object execute(String request, String config, String script) {
        if (script != null && !script.isBlank()) {
            return scriptToolExecutor.executeScript(script, request, config);
        }
        // Default MCP logic (optional fallback)
        return "MCP executed with config: " + config + ", request: " + request;
    }
}