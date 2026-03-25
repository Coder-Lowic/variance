package com.lowic.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ChatService {

    private final ModelManagerService modelManagerService;
    private final SpeechToTextService speechToTextService;

    public ChatService(ModelManagerService modelManagerService, SpeechToTextService speechToTextService) {
        this.modelManagerService = modelManagerService;
        this.speechToTextService = speechToTextService;
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

    public String chatWithImage(String message, String imageBase64, String imageName) {
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        
        // 简化实现，将图片作为base64文本包含在消息中
        String userPrompt = String.format("%s\n\n[Image: %s]\n%s", message, imageName, imageBase64);
        
        return chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();
    }

    public String chatWithImageAndSystemPrompt(String systemPrompt, String message, String imageBase64, String imageName) {
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        
        // 简化实现，将图片作为base64文本包含在消息中
        String userPrompt = String.format("%s\n\n[Image: %s]\n%s", message, imageName, imageBase64);
        
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
    }

    public String chatWithVoice(MultipartFile audioFile, String model) throws IOException {
        String text = speechToTextService.transcribe(audioFile, model);
        return chat(text);
    }

    public String chatWithVoiceAndSystemPrompt(String systemPrompt, MultipartFile audioFile, String model) throws IOException {
        String text = speechToTextService.transcribe(audioFile, model);
        return chatWithSystemPrompt(systemPrompt, text);
    }
}

