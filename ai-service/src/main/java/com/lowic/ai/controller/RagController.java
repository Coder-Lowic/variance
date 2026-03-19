package com.lowic.ai.controller;

import com.lowic.ai.service.RagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "RAG检索接口", description = "提供检索增强生成能力")
@RestController
@RequestMapping("/api/ai/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @Operation(summary = "添加文档", description = "向向量数据库添加文档")
    @PostMapping("/documents")
    public void addDocuments(@RequestBody List<Document> documents) {
        ragService.addDocuments(documents);
    }

    @Operation(summary = "RAG查询", description = "基于检索增强生成进行问答")
    @PostMapping("/query")
    public String ragQuery(
            @RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        int topK = request.get("topK") != null ? (Integer) request.get("topK") : 5;
        return ragService.ragQuery(query, topK);
    }
}
