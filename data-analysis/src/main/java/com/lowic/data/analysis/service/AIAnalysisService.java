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
        String prompt = String.format("""
                请基于以下业务数据生成一份详细的分析报告：
                
                %s
                
                报告应包括：
                1. 数据概览
                2. 关键指标分析
                3. 趋势分析
                4. 存在的问题
                5. 改进建议
                """, businessData);
        
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    public String analyzeAdPerformance(String adData) {
        String prompt = String.format("""
                请分析以下广告投放数据，评估广告效果：
                
                %s
                
                分析应包括：
                1. 广告投放效果概览
                2. 各渠道效果对比
                3. ROI分析
                4. 优化建议
                """, adData);
        
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    public String forecastSales(String historicalSales) {
        String prompt = String.format("""
                请基于以下历史销售数据，预测未来的销售趋势：
                
                %s
                
                预测应包括：
                1. 历史销售趋势分析
                2. 未来3个月销售预测
                3. 影响因素分析
                4. 建议措施
                """, historicalSales);
        
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
