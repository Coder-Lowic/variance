package com.lowic.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class DataAnalysisAIService {

    private final ChatClient chatClient;

    public DataAnalysisAIService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String generateAnalysisReport(String dataDescription) {
        String systemPrompt = """
                你是一位专业的数据分析师。请基于提供的数据描述，生成一份专业的数据分析报告。
                报告应包含：数据概览、关键指标分析、趋势洞察、建议与结论。
                请用中文回复，保持专业、客观、有建设性。
                """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(dataDescription)
                .call()
                .content();
    }

    public String detectAnomalies(String data) {
        String systemPrompt = """
                你是一位异常检测专家。请分析提供的数据，识别其中的异常值、异常模式或潜在问题。
                请指出异常的具体位置、可能的原因，并提供处理建议。
                """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(data)
                .call()
                .content();
    }

    public String predictTrend(String historicalData) {
        String systemPrompt = """
                你是一位趋势预测专家。请基于提供的历史数据，分析趋势模式并进行未来预测。
                请说明预测依据、预测结果、置信度以及潜在风险因素。
                """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(historicalData)
                .call()
                .content();
    }
}
