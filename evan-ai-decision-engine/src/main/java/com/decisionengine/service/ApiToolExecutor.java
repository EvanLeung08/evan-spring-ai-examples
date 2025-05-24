package com.decisionengine.service;

import org.springframework.stereotype.Service;

@Service
public class ApiToolExecutor implements ToolExecutor {
    @Override
    public String getToolType() { return "API"; }

    @Override
    public Object execute(String request, String config) {
        // 解析 config，调用外部 API，返回结果
        return "API executed with config: " + config + ", request: " + request;
    }
}