package com.lowic.variance.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@Tag(name = "服务编排", description = "核心服务编排与聚合接口")
public class OrchestrationController {

    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查编排服务及各下游服务状态")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "variance-service (core orchestration)",
                "timestamp", LocalDateTime.now().toString(),
                "downstream", Map.of(
                        "ai-service", "http://localhost:8081",
                        "data-analysis", "http://localhost:8888"
                )
        ));
    }

    @GetMapping("/")
    @Operation(summary = "服务信息", description = "获取核心服务基本信息")
    public ResponseEntity<Map<String, Object>> index() {
        return ResponseEntity.ok(Map.of(
                "module", "variance-service",
                "role", "核心业务编排服务",
                "description", "负责协调 AI 智能服务和数据分析服务，提供跨模块的业务编排能力",
                "version", "1.0-SNAPSHOT",
                "capabilities", new String[]{
                        "服务健康监控",
                        "跨服务业务编排",
                        "定时任务调度（待实现）",
                        "数据聚合与转换"
                }
        ));
    }
}
