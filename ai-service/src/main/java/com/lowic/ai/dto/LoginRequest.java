package com.lowic.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "登录请求")
public record LoginRequest(
        @NotBlank @Schema(description = "用户名", example = "admin") String username,
        @NotBlank @Schema(description = "密码", example = "password123") String password
) {}
