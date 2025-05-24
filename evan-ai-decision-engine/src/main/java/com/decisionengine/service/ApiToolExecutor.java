package com.decisionengine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiToolExecutor implements ToolExecutor {
    private final ScriptToolExecutor scriptToolExecutor;

    @Override
    public String getToolType() { return "API"; }

    @Override
    public Object execute(String request, String config, String script) {
        if (script != null && !script.isBlank()) {
            return scriptToolExecutor.executeScript(script, request, config);
        }
        // Default API logic (optional fallback)
        return "API executed with config: " + config + ", request: " + request;
    }
}