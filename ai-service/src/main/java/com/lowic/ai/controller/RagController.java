package com.lowic.ai.controller;

import com.lowic.ai.service.RagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Tag(name = "检索增强", description = "RAG检索增强相关的API接口")
@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @Operation(summary = "添加文本", description = "向向量存储中添加文本内容")
    @PostMapping("/text")
    public ResponseEntity<Map<String, Object>> addText(@RequestBody Map<String, Object> request) {
        String content = (String) request.get("content");
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) request.get("metadata");
        int count = ragService.addText(content, metadata);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "count", count
        ));
    }

    @Operation(summary = "添加图像", description = "向向量存储中添加图像")
    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> addImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "metadata", required = false) String metadataJson) throws IOException {
        Map<String, Object> metadata = metadataJson != null ? parseJson(metadataJson) : null;
        int count = ragService.addImage(file, metadata);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "count", count
        ));
    }

    @Operation(summary = "添加视频", description = "向向量存储中添加视频")
    @PostMapping("/video")
    public ResponseEntity<Map<String, Object>> addVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "metadata", required = false) String metadataJson) throws IOException {
        Map<String, Object> metadata = metadataJson != null ? parseJson(metadataJson) : null;
        int count = ragService.addVideo(file, metadata);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "count", count
        ));
    }

    @Operation(summary = "添加文档文件", description = "向向量存储中添加文档文件")
    @PostMapping("/document")
    public ResponseEntity<Map<String, Object>> addDocument(
            @RequestParam("file") MultipartFile file) throws IOException {
        String content = new String(file.getBytes());
        int count = ragService.addText(content, Map.of("fileName", file.getOriginalFilename()));
        return ResponseEntity.ok(Map.of(
                "success", true,
                "count", count
        ));
    }

    @Operation(summary = "搜索文档", description = "在向量存储中搜索相关文档")
    @GetMapping("/search")
    public ResponseEntity<List<Document>> searchDocuments(
            @RequestParam("query") String query,
            @RequestParam(value = "k", defaultValue = "5") int k) {
        List<Document> documents = ragService.searchDocuments(query, k);
        return ResponseEntity.ok(documents);
    }

    @Operation(summary = "RAG查询", description = "基于检索增强生成回答")
    @PostMapping("/query")
    public ResponseEntity<String> ragQuery(@RequestBody Map<String, Object> request) {
        String prompt = (String) request.get("prompt");
        int k = request.containsKey("k") ? (Integer) request.get("k") : 3;
        String answer = ragService.ragQuery(prompt, k);
        return ResponseEntity.ok(answer);
    }

    @Operation(summary = "删除文档", description = "从向量存储中删除文档")
    @DeleteMapping("/document/{id}")
    public ResponseEntity<Map<String, Object>> deleteDocument(@PathVariable String id) {
        ragService.deleteDocument(id);
        return ResponseEntity.ok(Map.of(
                "success", true
        ));
    }

    @Operation(summary = "获取文档总数", description = "获取向量存储中的文档总数")
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getDocumentCount() {
        long count = ragService.getDocumentCount();
        return ResponseEntity.ok(Map.of(
                "count", count
        ));
    }

    @Operation(summary = "清空文档", description = "清空向量存储中的所有文档")
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearDocuments() {
        ragService.clearDocuments();
        return ResponseEntity.ok(Map.of(
                "success", true
        ));
    }

    private Map<String, Object> parseJson(String json) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Map.class);
        } catch (Exception e) {
            return null;
        }
    }
}
