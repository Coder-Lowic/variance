package com.lowic.ai.controller;

import com.lowic.ai.service.PersonalizedRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "个性化推荐", description = "个性化推荐相关的API接口")
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final PersonalizedRecommendationService recommendationService;

    public RecommendationController(PersonalizedRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Operation(summary = "分析用户偏好", description = "分析用户的对话历史，提取用户偏好")
    @GetMapping("/users/{userId}/preferences")
    public ResponseEntity<Map<String, Object>> analyzeUserPreferences(@PathVariable String userId) {
        Map<String, Object> preferences = recommendationService.analyzeUserPreferences(userId);
        return ResponseEntity.ok(preferences);
    }

    @Operation(summary = "获取个性化推荐", description = "基于用户偏好生成个性化推荐")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<String>> getRecommendations(
            @PathVariable String userId,
            @RequestParam(value = "context", required = false) String context,
            @RequestParam(value = "count", defaultValue = "5") int count) {
        List<String> recommendations = recommendationService.generateRecommendations(userId, context, count);
        return ResponseEntity.ok(recommendations);
    }

    @Operation(summary = "获取RAG增强的个性化推荐", description = "基于RAG检索生成更精准的个性化推荐")
    @GetMapping("/users/{userId}/rag")
    public ResponseEntity<List<String>> getRAGBasedRecommendations(
            @PathVariable String userId,
            @RequestParam(value = "context", required = false) String context,
            @RequestParam(value = "count", defaultValue = "5") int count) {
        List<String> recommendations = recommendationService.generateRAGBasedRecommendations(userId, context, count);
        return ResponseEntity.ok(recommendations);
    }

    @Operation(summary = "生成个性化欢迎消息", description = "为用户生成个性化的欢迎消息")
    @GetMapping("/users/{userId}/welcome")
    public ResponseEntity<String> generateWelcomeMessage(@PathVariable String userId) {
        String message = recommendationService.generatePersonalizedWelcomeMessage(userId);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "生成个性化内容建议", description = "为用户生成个性化内容建议")
    @GetMapping("/users/{userId}/content")
    public ResponseEntity<List<String>> generateContentSuggestions(
            @PathVariable String userId,
            @RequestParam("contentType") String contentType,
            @RequestParam(value = "count", defaultValue = "5") int count) {
        List<String> suggestions = recommendationService.generateContentSuggestions(userId, contentType, count);
        return ResponseEntity.ok(suggestions);
    }

    @Operation(summary = "推荐文档", description = "为用户推荐相关文档")
    @GetMapping("/users/{userId}/documents")
    public ResponseEntity<List<Document>> recommendDocuments(
            @PathVariable String userId,
            @RequestParam("query") String query,
            @RequestParam(value = "count", defaultValue = "5") int count) {
        List<Document> documents = recommendationService.recommendDocuments(userId, query, count);
        return ResponseEntity.ok(documents);
    }

    @Operation(summary = "个性化问答", description = "基于用户偏好生成个性化回答")
    @PostMapping("/users/{userId}/answer")
    public ResponseEntity<String> generatePersonalizedAnswer(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {
        String question = request.get("question");
        String answer = recommendationService.generatePersonalizedAnswer(userId, question);
        return ResponseEntity.ok(answer);
    }

    @Operation(summary = "生成个性化内容", description = "根据模板和用户偏好生成个性化内容")
    @PostMapping("/users/{userId}/generate")
    public ResponseEntity<String> generatePersonalizedContent(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {
        String template = request.get("template");
        String content = recommendationService.generatePersonalizedContent(userId, template);
        return ResponseEntity.ok(content);
    }
}
