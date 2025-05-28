package com.decisionengine.service;

import com.decisionengine.model.Workflow;
import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AIDecisionService {
    private static final Logger log = LoggerFactory.getLogger(AIDecisionService.class);

    private final ChatClient chatClient;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    private final String promptTemplate = """
        分析用户请求内容，根据以下工具进行路由决策：
        工具列表：${tools}
        请求内容：${request}
        返回JSON格式：{ "tool": "...", "confidence": 0.95 }
        """;

    public AIDecisionService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public ToolDecision makeDecision(String userRequest, List<String> tools) {
        String prompt = promptTemplate
                .replace("${tools}", String.join(",", tools))
                .replace("${request}", userRequest);

        String aiResponse = chatClient.prompt(prompt).call().content();
        try {
            // Remove code block markers if present
            String json = aiResponse.trim();
            if (json.startsWith("```")) {
                int start = json.indexOf("{");
                int end = json.lastIndexOf("}");
                if (start >= 0 && end >= 0 && end > start) {
                    json = json.substring(start, end + 1);
                }
            }
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            String tool = (String) map.get("tool");
            double confidence = Double.parseDouble(map.get("confidence").toString());
            if (confidence < 0.8) {
                log.warn("AI decision confidence low: {}", aiResponse);
            }
            return new ToolDecision(tool, confidence);
        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", aiResponse, e);
            return new ToolDecision(null, 0.0);
        }
    }
    public WorkflowDecision selectWorkflow(String userRequest, List<Workflow> workflows) {
        if (workflows.isEmpty()) {
            return new WorkflowDecision(null, 0.0);
        }

        String workflowNames = workflows.stream()
                .map(w -> w.getName() + (w.getDescription() != null ? " (" + w.getDescription() + ")" : ""))
                .collect(Collectors.joining(", "));

        String prompt = """
        分析用户请求内容，从以下工作流中选择最适合的一个：
        工作流列表：${workflows}
        请求内容：${request}
        返回JSON格式：{ "workflow": "...", "confidence": 0.95 }
        """.replace("${workflows}", workflowNames)
                .replace("${request}", userRequest);

        String aiResponse = chatClient.prompt(prompt).call().content();
        try {
            // Process response and extract workflow name and confidence
            String json = aiResponse.trim();
            if (json.startsWith("```")) {
                int start = json.indexOf("{");
                int end = json.lastIndexOf("}");
                if (start >= 0 && end >= 0 && end > start) {
                    json = json.substring(start, end + 1);
                }
            }
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            String workflowName = (String) map.get("workflow");
            double confidence = Double.parseDouble(map.get("confidence").toString());

            return new WorkflowDecision(workflowName, confidence);
        } catch (Exception e) {
            log.error("Failed to parse AI response for workflow selection: {}", aiResponse, e);
            return new WorkflowDecision(null, 0.0);
        }
    }

    public record WorkflowDecision(String workflowName, double confidence) {}
    public record ToolDecision(String tool, double confidence) {}
}