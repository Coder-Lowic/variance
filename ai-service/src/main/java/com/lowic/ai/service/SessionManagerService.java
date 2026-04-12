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
    private final Map<String, Map<String, Object>> userPreferences = new HashMap<>();

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

    // 存储用户偏好数据
    public void saveUserPreferences(String userId, Map<String, Object> preferences) {
        userPreferences.put(userId, preferences);
    }

    // 获取用户偏好数据
    public Map<String, Object> getUserPreferences(String userId) {
        return userPreferences.getOrDefault(userId, new HashMap<>());
    }

    // 清除用户偏好数据
    public void clearUserPreferences(String userId) {
        userPreferences.remove(userId);
    }

    // 检查用户是否有偏好数据
    public boolean hasUserPreferences(String userId) {
        return userPreferences.containsKey(userId);
    }

    // 获取用户的会话数量
    public int getUserSessionCount(String userId) {
        return listSessions(userId).size();
    }

    // 获取用户的总消息数
    public int getUserMessageCount(String userId) {
        return listSessions(userId).stream()
                .mapToInt(session -> session.getMessages().size())
                .sum();
    }
}
