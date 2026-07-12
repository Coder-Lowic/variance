package com.lowic.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "文档对比请求")
public record CompareDocumentsRequest(
        @Schema(description = "文档1 ID") String docId1,
        @Schema(description = "文档2 ID") String docId2
) {}
