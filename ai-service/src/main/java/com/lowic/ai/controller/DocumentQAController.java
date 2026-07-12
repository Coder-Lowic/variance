package com.lowic.ai.controller;

import com.lowic.ai.dto.CompareDocumentsRequest;
import com.lowic.ai.dto.DocumentAskRequest;
import com.lowic.ai.dto.DocumentSummarizeRequest;
import com.lowic.ai.entity.DocumentQAResult;
import com.lowic.ai.service.DocumentQAService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "文档智能问答", description = "上传文档后进行智能问答、摘要、关键信息提取等功能")
@RestController
@RequestMapping("/api/document-qa")
public class DocumentQAController {

    private final DocumentQAService documentQAService;

    public DocumentQAController(DocumentQAService documentQAService) {
        this.documentQAService = documentQAService;
    }

    @Operation(summary = "上传文档", description = "上传文档并缓存内容，返回文档ID用于后续问答")
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadDocument(@RequestParam("file") MultipartFile file) throws Exception {
        Map<String, Object> result = documentQAService.uploadDocument(file);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "文档问答", description = "基于已上传的文档ID进行问答")
    @PostMapping("/ask")
    public ResponseEntity<DocumentQAResult> askQuestion(@RequestBody DocumentAskRequest request) {
        DocumentQAResult result = documentQAService.askQuestion(request.docId(), request.question(), request.sessionId());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "上传文档并直接问答", description = "上传文档并立即提问，一步完成")
    @PostMapping("/ask-with-file")
    public ResponseEntity<DocumentQAResult> askQuestionWithFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("question") String question,
            @RequestParam(value = "sessionId", required = false) String sessionId) throws Exception {
        DocumentQAResult result = documentQAService.askQuestionWithFile(file, question, sessionId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "文本内容直接问答", description = "直接提供文本内容进行问答，无需上传文件")
    @PostMapping("/ask-with-content")
    public ResponseEntity<DocumentQAResult> askQuestionWithContent(@RequestBody Map<String, Object> request) {
        String content = (String) request.get("content");
        String question = (String) request.get("question");
        String documentName = (String) request.getOrDefault("documentName", "text-input");
        String sessionId = (String) request.getOrDefault("sessionId", null);
        DocumentQAResult result = documentQAService.askQuestionWithContent(content, documentName, question, sessionId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "文档摘要生成", description = "生成文档的摘要，支持short/medium/detailed三种长度")
    @PostMapping("/summarize")
    public ResponseEntity<DocumentQAResult> summarizeDocument(@RequestBody DocumentSummarizeRequest request) {
        String summaryLength = request.summaryLength() != null ? request.summaryLength() : "medium";
        DocumentQAResult result = documentQAService.summarizeDocument(request.docId(), summaryLength);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "文档关键信息提取", description = "提取文档的主题、核心观点、重要数据等关键信息")
    @PostMapping("/key-points")
    public ResponseEntity<DocumentQAResult> extractKeyPoints(@RequestBody DocumentAskRequest request) {
        DocumentQAResult result = documentQAService.extractKeyPoints(request.docId());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "文档对比分析", description = "对比两个文档的相似之处和不同之处")
    @PostMapping("/compare")
    public ResponseEntity<DocumentQAResult> compareDocuments(@RequestBody CompareDocumentsRequest request) {
        DocumentQAResult result = documentQAService.compareDocuments(request.docId1(), request.docId2());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "基于RAG的文档问答", description = "使用检索增强生成技术进行文档问答")
    @PostMapping("/ask-with-rag")
    public ResponseEntity<DocumentQAResult> askQuestionWithRAG(@RequestBody Map<String, Object> request) {
        String docId = (String) request.get("docId");
        String question = (String) request.get("question");
        int k = request.containsKey("k") ? ((Number) request.get("k")).intValue() : 3;
        String sessionId = (String) request.getOrDefault("sessionId", null);
        DocumentQAResult result = documentQAService.askQuestionWithRAG(docId, question, k, sessionId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "获取会话问答历史", description = "获取指定会话的所有问答历史记录")
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<DocumentQAResult>> getSessionHistory(@PathVariable String sessionId) {
        List<DocumentQAResult> history = documentQAService.getSessionHistory(sessionId);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "清理会话历史", description = "清理指定会话的问答历史")
    @DeleteMapping("/history/{sessionId}")
    public ResponseEntity<Map<String, Object>> clearSessionHistory(@PathVariable String sessionId) {
        documentQAService.clearSessionHistory(sessionId);
        return ResponseEntity.ok(Map.of("success", true, "message", "会话历史已清理"));
    }

    @Operation(summary = "删除缓存的文档", description = "删除已上传缓存的文档")
    @DeleteMapping("/document/{docId}")
    public ResponseEntity<Map<String, Object>> removeDocument(@PathVariable String docId) {
        documentQAService.removeDocument(docId);
        return ResponseEntity.ok(Map.of("success", true, "message", "文档已删除"));
    }

    @Operation(summary = "获取会话ID", description = "创建一个新的问答会话ID")
    @PostMapping("/session")
    public ResponseEntity<Map<String, Object>> createSession() {
        String sessionId = UUID.randomUUID().toString();
        return ResponseEntity.ok(Map.of("sessionId", sessionId));
    }
}
