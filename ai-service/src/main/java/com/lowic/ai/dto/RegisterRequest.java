package com.lowic.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "注册请求")
public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 64) @Schema(description = "用户名", example = "admin") String username,
        @NotBlank @Size(min = 6, max = 128) @Schema(description = "密码", example = "password123") String password,
        @Email @Schema(description = "邮箱（可选）", example = "admin@example.com") String email
) {}
