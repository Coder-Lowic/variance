package com.lowic.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "对话请求")
public record ChatRequest(
        @Schema(description = "用户消息", example = "你好") String message,
        @Schema(description = "会话ID（可选）") String sessionId,
        @Schema(description = "系统提示词（可选）") String systemPrompt,
        @Schema(description = "RAG检索文档数", example = "3") Integer k
) {}
