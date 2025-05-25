package com.decisionengine.mcp;

import io.modelcontextprotocol.spec.McpSchema;

import java.util.Arrays;
import java.util.Map;

public class MCPTest {
    public static void main(String[] args) {
        // 测试 stdio 传输方式
/*        try {
            McpConfig stdioConfig = new McpConfig();
            stdioConfig.setTransportType("stdio");
            stdioConfig.setServerCommand("npx"); // 请根据实际服务器设置调整
            stdioConfig.setServerArgs(Arrays.asList("-y", "@modelcontextprotocol/server-everything", "dir")); // 请根据实际服务器设置调整
            McpUtils stdioUtil = new McpUtils(stdioConfig);
            Map<String, Object> stdioParams = Map.of("operation", "add", "a", 2, "b", 3);
            McpSchema.CallToolResult stdioResult = stdioUtil.callTool("calculator", stdioParams);
            System.out.println("Stdio tool call result: " + stdioResult);
            stdioUtil.close();
        } catch (Exception e) {
            System.out.println("Error in stdio test: " + e.getMessage());
        }*/

        // 测试 SSE 传输方式
        try {
            McpConfig sseConfig = new McpConfig();
            sseConfig.setTransportType("sse");
            sseConfig.setServerUrl("http://localhost:8999"); // 请替换为实际服务器 URL
            McpUtils sseUtil = new McpUtils(sseConfig);


            //McpSchema.CallToolResult sseResult = sseUtil.callTool("getMcpLogMessage", null);
            Map<String, Object> sseParams = Map.of("input", "test" );
            McpSchema.CallToolResult sseResult = sseUtil.callTool("executeBasicTool", sseParams);
            System.out.println("SSE tool call result: " + sseResult);
            sseUtil.close();
        } catch (Exception e) {
            System.out.println("Error in SSE test: " + e.getMessage());
        }
    }
}