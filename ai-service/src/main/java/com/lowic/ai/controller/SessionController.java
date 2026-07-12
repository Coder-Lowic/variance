package com.lowic.ai.controller;

import com.lowic.ai.dto.SessionCreateRequest;
import com.lowic.ai.entity.ChatSession;
import com.lowic.ai.service.SessionManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "会话管理", description = "会话管理相关的API接口")
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionManagerService sessionManagerService;

    public SessionController(SessionManagerService sessionManagerService) {
        this.sessionManagerService = sessionManagerService;
    }

    @Operation(summary = "创建会话", description = "创建新的对话会话")
    @PostMapping
    public ResponseEntity<ChatSession> createSession(@RequestBody SessionCreateRequest request) {
        ChatSession session = sessionManagerService.createSession(request.userId());
        return ResponseEntity.ok(session);
    }

    @Operation(summary = "获取会话列表", description = "获取用户的所有会话列表")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatSession>> listSessions(@PathVariable String userId) {
        List<ChatSession> sessions = sessionManagerService.listSessions(userId);
        return ResponseEntity.ok(sessions);
    }

    @Operation(summary = "获取会话详情", description = "获取指定会话的详细信息")
    @GetMapping("/{sessionId}")
    public ResponseEntity<ChatSession> getSession(@PathVariable String sessionId) {
        ChatSession session = sessionManagerService.getSession(sessionId);
        if (session != null) {
            return ResponseEntity.ok(session);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "删除会话", description = "删除指定会话")
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<java.util.Map<String, Object>> deleteSession(@PathVariable String sessionId) {
        sessionManagerService.deleteSession(sessionId);
        return ResponseEntity.ok(java.util.Map.of(
                "success", true
        ));
    }

    @Operation(summary = "获取会话消息", description = "获取指定会话的所有消息")
    @GetMapping("/{sessionId}/messages")
    public ResponseEntity<ChatSession> getSessionMessages(@PathVariable String sessionId) {
        ChatSession session = sessionManagerService.getSession(sessionId);
        if (session != null) {
            return ResponseEntity.ok(session);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
