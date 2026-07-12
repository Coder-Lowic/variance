package com.lowic.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "文档摘要请求")
public record DocumentSummarizeRequest(
        @Schema(description = "文档ID", example = "abc-123") String docId,
        @Schema(description = "摘要长度（short/medium/detailed）", example = "medium") String summaryLength
) {}
