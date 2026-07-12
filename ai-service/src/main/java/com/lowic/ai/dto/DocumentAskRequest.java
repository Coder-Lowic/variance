package com.lowic.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "文档问答请求")
public record DocumentAskRequest(
        @Schema(description = "文档ID", example = "abc-123") String docId,
        @Schema(description = "提问内容", example = "文档的主题是什么？") String question,
        @Schema(description = "会话ID（可选）") String sessionId
) {}
