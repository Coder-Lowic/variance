package com.lowic.data.analysis.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIAnalysisService {

    private final ChatClient chatClient;

    public AIAnalysisService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String generateReport(String businessData) {
        String systemPrompt = """
                你是一位专业的商业智能分析师。请基于提供的业务数据，生成一份专业的分析报告。
                报告结构要求：
                1. 数据概览：简要描述数据的基本情况
                2. 关键指标：识别并分析核心业务指标
                3. 趋势洞察：发现数据中的趋势和模式
                4. 问题诊断：识别潜在的问题和风险
                5. 优化建议：提供可执行的改进建议
                请用中文回复，保持专业、客观、有数据支撑。
                """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(businessData)
                .call()
                .content();
    }

    public String analyzeAdPerformance(String adData) {
        String systemPrompt = """
                你是一位广告投放优化专家。请分析提供的广告数据，评估广告投放效果。
                请从以下维度分析：
                1. 曝光量、点击量、转化率分析
                2. ROI（投资回报率）评估
                3. 不同渠道/广告位的效果对比
                4. 优化建议和投放策略调整
                """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(adData)
                .call()
                .content();
    }

    public String forecastSales(String historicalSales) {
        String systemPrompt = """
                你是一位销售预测专家。请基于历史销售数据进行预测分析。
                分析内容应包括：
                1. 历史趋势分析
                2. 季节性因素识别
                3. 未来3-6个月销售预测
                4. 影响因素分析和风险提示
                """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(historicalSales)
                .call()
                .content();
    }
}
