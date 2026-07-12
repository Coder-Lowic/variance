package com.lowic.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "创建会话请求")
public record SessionCreateRequest(
        @Schema(description = "用户ID", example = "user-001") String userId
) {}
