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

    public String chatWithAttachment(String message, String attachmentContent, String attachmentName) {
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        
        String userPrompt = String.format("""
                用户问题：%s
                
                附件内容（文件名：%s）：
                %s
                
                请基于附件内容回答用户的问题。
                """, message, attachmentName, attachmentContent);
        
        return chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();
    }

    public String chatWithAttachmentAndSystemPrompt(String systemPrompt, String message, String attachmentContent, String attachmentName) {
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        
        String userPrompt = String.format("""
                用户问题：%s
                
                附件内容（文件名：%s）：
                %s
                
                请基于附件内容回答用户的问题。
                """, message, attachmentName, attachmentContent);
        
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
    }
}
