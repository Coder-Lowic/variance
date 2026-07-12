package com.lowic.ai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ──── 业务异常 ────

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSessionNotFound(SessionNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, "SESSION_NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDocumentNotFound(DocumentNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, "DOCUMENT_NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler(DocumentProcessingException.class)
    public ResponseEntity<Map<String, Object>> handleDocumentProcessing(DocumentProcessingException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "DOCUMENT_PROCESSING_ERROR", e.getMessage());
    }

    @ExceptionHandler(ModerationException.class)
    public ResponseEntity<Map<String, Object>> handleModerationError(ModerationException e) {
        Map<String, Object> body = buildBody("MODERATION_ERROR", e.getMessage());
        if (e.getContentId() != null) {
            body.put("contentId", e.getContentId());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "ILLEGAL_ARGUMENT", e.getMessage());
    }

    // ──── 文件上传异常 ────

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, Object>> handleMultipart(MultipartException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "MULTIPART_ERROR", "文件上传失败：" + e.getMessage());
    }

    // ──── 兜底异常 ────

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "RUNTIME_ERROR", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "系统内部错误：" + e.getMessage());
    }

    // ──── 工具方法 ────

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String type, String message) {
        return ResponseEntity.status(status).body(buildBody(type, message));
    }

    private Map<String, Object> buildBody(String type, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("type", type);
        body.put("message", message);
        return body;
    }
}
