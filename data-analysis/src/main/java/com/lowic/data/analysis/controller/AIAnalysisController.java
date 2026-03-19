package com.lowic.data.analysis.controller;

import com.lowic.data.analysis.service.AIAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI数据分析接口", description = "基于Spring AI的智能数据分析能力")
@RestController
@RequestMapping("/api/analysis/ai")
public class AIAnalysisController {

    private final AIAnalysisService aiAnalysisService;

    public AIAnalysisController(AIAnalysisService aiAnalysisService) {
        this.aiAnalysisService = aiAnalysisService;
    }

    @Operation(summary = "生成分析报告", description = "基于业务数据生成AI智能分析报告")
    @PostMapping("/report")
    public String generateReport(@RequestBody String businessData) {
        return aiAnalysisService.generateReport(businessData);
    }

    @Operation(summary = "广告效果分析", description = "AI驱动的广告投放效果分析")
    @PostMapping("/ad-performance")
    public String analyzeAdPerformance(@RequestBody String adData) {
        return aiAnalysisService.analyzeAdPerformance(adData);
    }

    @Operation(summary = "销售预测", description = "基于历史数据进行AI销售预测")
    @PostMapping("/forecast")
    public String forecastSales(@RequestBody String historicalSales) {
        return aiAnalysisService.forecastSales(historicalSales);
    }
}
