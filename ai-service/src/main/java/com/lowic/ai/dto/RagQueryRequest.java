package com.lowic.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "RAG查询请求")
public record RagQueryRequest(
        @Schema(description = "查询文本", example = "什么是RAG？") String query,
        @Schema(description = "检索文档数量", example = "3") Integer k
) {}
