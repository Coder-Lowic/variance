package com.lowic.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "模型切换请求")
public record ModelSwitchRequest(
        @Schema(description = "AI提供商", example = "openai") String provider,
        @Schema(description = "模型名称", example = "gpt-4") String model
) {}
