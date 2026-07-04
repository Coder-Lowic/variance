package com.lowic.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowic.ai.entity.ChatSession;
import com.lowic.ai.entity.UserPreference;
import com.lowic.ai.repository.ChatSessionRepository;
import com.lowic.ai.repository.UserPreferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SessionManagerService {
    private static final Logger log = LoggerFactory.getLogger(SessionManagerService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ChatSessionRepository sessionRepository;
    private final UserPreferenceRepository preferenceRepository;

    public SessionManagerService(ChatSessionRepository sessionRepository,
                                 UserPreferenceRepository preferenceRepository) {
        this.sessionRepository = sessionRepository;
        this.preferenceRepository = preferenceRepository;
    }

    @Transactional
    public ChatSession createSession(String userId) {
        String sessionId = UUID.randomUUID().toString();
        ChatSession session = new ChatSession(sessionId, userId);
        ChatSession saved = sessionRepository.save(session);
        log.debug("Created session {} for user {}", sessionId, userId);
        return saved;
    }

    @Transactional(readOnly = true)
    public ChatSession getSession(String sessionId) {
        return sessionRepository.findById(sessionId).orElse(null);
    }

    @Transactional
    public void saveSession(ChatSession session) {
        sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public List<ChatSession> listSessions(String userId) {
        return sessionRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    @Transactional
    public void deleteSession(String sessionId) {
        sessionRepository.deleteById(sessionId);
        log.debug("Deleted session {}", sessionId);
    }

    // === 用户偏好（持久化到数据库） ===

    @Transactional
    public void saveUserPreferences(String userId, Map<String, Object> preferences) {
        try {
            String json = objectMapper.writeValueAsString(preferences);
            UserPreference pref = new UserPreference(userId, json);
            preferenceRepository.save(pref);
            log.debug("Saved preferences for user {}", userId);
        } catch (Exception e) {
            log.error("Failed to save preferences for user {}: {}", userId, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserPreferences(String userId) {
        return preferenceRepository.findById(userId)
                .map(UserPreference::getPreferencesJson)
                .map(this::parsePreferences)
                .orElseGet(HashMap::new);
    }

    @Transactional
    public void clearUserPreferences(String userId) {
        preferenceRepository.deleteById(userId);
        log.debug("Cleared preferences for user {}", userId);
    }

    @Transactional(readOnly = true)
    public boolean hasUserPreferences(String userId) {
        return preferenceRepository.existsById(userId);
    }

    @Transactional(readOnly = true)
    public int getUserSessionCount(String userId) {
        return (int) sessionRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public int getUserMessageCount(String userId) {
        return listSessions(userId).stream()
                .mapToInt(session -> session.getMessages() != null ? session.getMessages().size() : 0)
                .sum();
    }

    private Map<String, Object> parsePreferences(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse user preferences JSON: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}
