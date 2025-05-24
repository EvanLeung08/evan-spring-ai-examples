package com.decisionengine.service;

public interface ToolExecutor {
    String getToolType();
    Object execute(String request, String config, String script);
}