package com.lowic.variance.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@Tag(name = "入口", description = "系统入口与健康检查")
public class IndexController {

    @GetMapping("/")
    @Operation(summary = "欢迎页", description = "返回系统基本信息")
    public ResponseEntity<Map<String, Object>> index() {
        return ResponseEntity.ok(Map.of(
                "name", "Variance — 智能数据分析平台",
                "version", "1.0-SNAPSHOT",
                "description", "集成 Spring AI 的智能数据分析和 AI 对话平台",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查服务运行状态")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "variance-web",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    @GetMapping("/services")
    @Operation(summary = "服务导航", description = "查看各服务的 API 文档地址")
    public ResponseEntity<Map<String, Object>> services() {
        return ResponseEntity.ok(Map.of(
                "swagger-ui", "/swagger-ui.html",
                "ai-service", "http://localhost:8081/swagger-ui.html",
                "data-analysis", "http://localhost:8888/swagger-ui.html"
        ));
    }
}
