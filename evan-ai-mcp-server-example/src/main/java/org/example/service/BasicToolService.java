package org.example.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class BasicToolService {

    @Tool(
            name = "executeBasicTool",
            description = "Returns input string with prefix"
    )
    public String executeBasicTool(
            @ToolParam String input
    ) {
        return "Processed: " + input;
    }
}