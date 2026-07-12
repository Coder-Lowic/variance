package com.lowic.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "内容审核请求")
public record ModerationRequest(
        @Schema(description = "待审核内容", example = "这是一段测试文本") String content,
        @Schema(description = "内容ID（可选）") String contentId
) {}
