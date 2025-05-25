package com.decisionengine.mcp;


import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.Map;


// 工具类，用于调用 MCP 服务器工具
public class McpUtils {
    private McpSyncClient client;

    // 构造函数，根据配置初始化客户端
    public McpUtils(McpConfig config) throws Exception {
        McpClientTransport transport;
        if ("stdio".equals(config.getTransportType())) {
            if (config.getServerCommand() == null || config.getServerArgs() == null) {
                throw new IllegalArgumentException("对于 stdio 传输，必须提供 serverCommand 和 serverArgs");
            }
            ServerParameters params = ServerParameters.builder(config.getServerCommand())
                    .args(config.getServerArgs().toArray(new String[0]))
                    .build();
            transport = new StdioClientTransport(params);
        } else if ("sse".equals(config.getTransportType())) {
            if (config.getServerUrl() == null || config.getServerUrl().isEmpty()) {
                throw new IllegalArgumentException("对于 SSE 传输，必须提供 serverUrl");
            }
            WebClient.Builder webClientBuilder = WebClient.builder()
                    .baseUrl(config.getServerUrl());
            transport = new WebFluxSseClientTransport(webClientBuilder);
        } else {
            throw new IllegalArgumentException("不支持的传输类型: " + config.getTransportType());
        }
        this.client = McpClient.sync(transport).build();  // or sync, depending on usage
        this.client.initialize();  // Add this line to initialize the client
    }

    // 调用 MCP 服务器上的工具
    public McpSchema.CallToolResult callTool(String toolName, Map<String, Object> params) {

        //Collections.emptyMap() is used to avoid null pointer exceptions if params is null
        McpSchema.CallToolRequest request = new McpSchema.CallToolRequest(toolName, params);

        return client.callTool(request);
    }

    // 可选：关闭客户端
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}