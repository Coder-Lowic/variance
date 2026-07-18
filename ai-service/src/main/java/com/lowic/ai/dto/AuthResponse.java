package com.lowic.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "认证响应")
public record AuthResponse(
        @Schema(description = "JWT Token") String token,
        @Schema(description = "Token 类型") String tokenType,
        @Schema(description = "用户名") String username
) {
    public static AuthResponse of(String token, String username) {
        return new AuthResponse(token, "Bearer", username);
    }
}
