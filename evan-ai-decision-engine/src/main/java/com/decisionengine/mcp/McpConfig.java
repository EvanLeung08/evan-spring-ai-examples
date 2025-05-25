package com.decisionengine.mcp;

import java.util.List;

// 配置类，用于存储 MCP 设置
public class McpConfig {
    private String transportType; // "stdio" 或 "sse"
    private String serverCommand; // stdio 传输需要的服务器命令
    private List<String> serverArgs; // stdio 传输需要的服务器命令参数
    private String serverUrl; // SSE 传输需要的服务器 URL

    // Getters 和 Setters
    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getServerCommand() {
        return serverCommand;
    }

    public void setServerCommand(String serverCommand) {
        this.serverCommand = serverCommand;
    }

    public List<String> getServerArgs() {
        return serverArgs;
    }

    public void setServerArgs(List<String> serverArgs) {
        this.serverArgs = serverArgs;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}