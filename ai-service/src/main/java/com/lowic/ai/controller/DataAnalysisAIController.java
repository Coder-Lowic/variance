package com.lowic.ai.controller;

import com.lowic.ai.service.DataAnalysisAIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "数据分析AI接口", description = "提供AI驱动的数据分析能力")
@RestController
@RequestMapping("/api/ai/analysis")
public class DataAnalysisAIController {

    private final DataAnalysisAIService dataAnalysisAIService;

    public DataAnalysisAIController(DataAnalysisAIService dataAnalysisAIService) {
        this.dataAnalysisAIService = dataAnalysisAIService;
    }

    @Operation(summary = "生成分析报告", description = "基于数据描述生成专业的数据分析报告")
    @PostMapping("/report")
    public String generateAnalysisReport(@RequestBody String dataDescription) {
        return dataAnalysisAIService.generateAnalysisReport(dataDescription);
    }

    @Operation(summary = "异常检测", description = "检测数据中的异常值和异常模式")
    @PostMapping("/anomalies")
    public String detectAnomalies(@RequestBody String data) {
        return dataAnalysisAIService.detectAnomalies(data);
    }

    @Operation(summary = "趋势预测", description = "基于历史数据进行趋势预测")
    @PostMapping("/predict")
    public String predictTrend(@RequestBody String historicalData) {
        return dataAnalysisAIService.predictTrend(historicalData);
    }
}
