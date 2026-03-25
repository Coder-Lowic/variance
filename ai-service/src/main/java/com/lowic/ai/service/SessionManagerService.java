package com.lowic.ai.service;

import com.lowic.ai.entity.ChatSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SessionManagerService {
    private final Map<String, ChatSession> sessions = new HashMap<>();

    public ChatSession createSession(String userId) {
        String sessionId = UUID.randomUUID().toString();
        ChatSession session = new ChatSession(sessionId, userId);
        sessions.put(sessionId, session);
        return session;
    }

    public ChatSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void saveSession(ChatSession session) {
        sessions.put(session.getSessionId(), session);
    }

    public List<ChatSession> listSessions(String userId) {
        return sessions.values().stream()
                .filter(session -> session.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public void deleteSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
