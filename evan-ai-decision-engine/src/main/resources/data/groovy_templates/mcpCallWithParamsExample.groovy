import groovy.json.JsonSlurper
import com.decisionengine.mcp.McpConfig
import com.decisionengine.mcp.McpUtils

return { request, config ->
    def cfgMap = new JsonSlurper().parseText(config)
    def mcpConfig = new McpConfig()
    mcpConfig.transportType = cfgMap.transportType ?: "sse"
    mcpConfig.serverUrl = cfgMap.serverUrl ?: "http://localhost:8999"

    def toolName = cfgMap.tool ?: "executeBasicTool"
    // Always build a map for parameters, as in MCPTest
    def params = cfgMap.parameters ?: [input: (request instanceof String ? request : (request?.toString() ?: ""))]
    print("Calling MCP tool: ${toolName} with parameters: ${params}")
    def mcpUtils = new McpUtils(mcpConfig)
    try {
        def result = mcpUtils.callTool(toolName, params)
        return result
    } finally {
        mcpUtils.close()
    }
}
/**
 * HTTP/SSE:
 {   "transportType": "sse",   "serverUrl": "http://localhost:8999",   "tool": "executeBasicTool" }
 STDIO:
 {"transport":"STDIO","command":["python3","my_tool.py"]}
 */