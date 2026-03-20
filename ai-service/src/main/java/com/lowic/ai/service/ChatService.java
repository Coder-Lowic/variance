package com.lowic.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ModelManagerService modelManagerService;

    public ChatService(ModelManagerService modelManagerService) {
        this.modelManagerService = modelManagerService;
    }

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
}
