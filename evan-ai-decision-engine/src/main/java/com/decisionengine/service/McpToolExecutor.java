package com.decisionengine.service;

import org.springframework.stereotype.Service;

@Service
public class McpToolExecutor implements ToolExecutor {
    @Override
    public String getToolType() { return "MCP"; }

    @Override
    public Object execute(String request, String config) {
        // 解析 config，执行 MCP 逻辑
        return "MCP executed with config: " + config + ", request: " + request;
    }
}