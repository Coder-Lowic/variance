package com.lowic.ai.service;

import com.lowic.ai.entity.ChatSession;
import com.lowic.ai.exception.SessionNotFoundException;
import com.lowic.ai.model.ModelConfig;
import com.lowic.ai.model.ModelProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * 核心对话服务 — 仅负责聊天与会话管理。
 * 文档生成、多模态分析、个性化推荐等请直接注入对应 Service。
 */
@Service
public class ChatService {

    private final ModelManagerService modelManagerService;
    private final SessionManagerService sessionManagerService;
    private final RagService ragService;

    public ChatService(ModelManagerService modelManagerService,
                       SessionManagerService sessionManagerService,
                       RagService ragService) {
        this.modelManagerService = modelManagerService;
        this.sessionManagerService = sessionManagerService;
        this.ragService = ragService;
    }

    // ──── 基础对话 ────

    public String chat(String message) {
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    public String chatWithSystemPrompt(String systemPrompt, String userMessage) {
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();
    }

    // ──── 会话对话 ────

    public String chatWithSession(String sessionId, String message) {
        ChatSession session = requireSession(sessionId);
        session.addMessage("user", message);

        String response = chat(buildConversationContext(session, message));
        session.addMessage("assistant", response);
        sessionManagerService.saveSession(session);

        return response;
    }

    public String chatWithSessionAndSystemPrompt(String sessionId, String systemPrompt, String message) {
        ChatSession session = requireSession(sessionId);
        session.addMessage("user", message);

        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(buildConversationContext(session, message))
                .call()
                .content();

        session.addMessage("assistant", response);
        sessionManagerService.saveSession(session);

        return response;
    }

    // ──── RAG 对话 ────

    public String chatWithRAG(String message, int k) {
        return ragService.ragQuery(message, k);
    }

    public String chatWithRAGAndImage(MultipartFile imageFile, String prompt, int k) throws IOException {
        return ragService.ragQueryWithImage(imageFile, prompt, k);
    }

    // ──── 模型管理 ────

    public void switchModel(String provider, String model) {
        ModelConfig config = new ModelConfig();
        config.setProvider(ModelProvider.valueOf(provider.toUpperCase()));
        config.setModelName(model);
        config.setTemperature(0.7);
        config.setMaxTokens(2048);
        modelManagerService.switchModel(config);
    }

    public List<Map<String, String>> getAvailableModels() {
        List<Map<String, String>> models = new ArrayList<>();
        for (var entry : modelManagerService.getAvailableModels().entrySet()) {
            Map<String, String> item = new HashMap<>();
            item.put("provider", entry.getKey().name());
            item.put("models", entry.getValue());
            models.add(item);
        }
        return models;
    }

    public Map<String, String> getCurrentModel() {
        ModelConfig config = modelManagerService.getCurrentModelConfig();
        return Map.of(
                "provider", config.getProvider().name(),
                "model", config.getModelName()
        );
    }

    // ──── 私有方法 ────

    private ChatSession requireSession(String sessionId) {
        ChatSession session = sessionManagerService.getSession(sessionId);
        if (session == null) {
            throw new SessionNotFoundException(sessionId);
        }
        return session;
    }

    private String buildConversationContext(ChatSession session, String currentMessage) {
        StringBuilder sb = new StringBuilder();
        for (var msg : session.getMessages()) {
            if ("user".equals(msg.getRole())) {
                sb.append("用户：").append(msg.getContent()).append("\n");
            } else if ("assistant".equals(msg.getRole())) {
                sb.append("助手：").append(msg.getContent()).append("\n");
            }
        }
        sb.append("用户：").append(currentMessage).append("\n助手：");
        return sb.toString();
    }
}
