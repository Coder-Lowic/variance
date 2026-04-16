package com.lowic.ai.service;

import com.lowic.ai.entity.ChatSession;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final ModelManagerService modelManagerService;
    private final SpeechToTextService speechToTextService;
    private final SessionManagerService sessionManagerService;
    private final MultimodalService multimodalService;
    private final DocumentGeneratorService documentGeneratorService;

    public ChatService(ModelManagerService modelManagerService, SpeechToTextService speechToTextService, SessionManagerService sessionManagerService, MultimodalService multimodalService, DocumentGeneratorService documentGeneratorService) {
        this.modelManagerService = modelManagerService;
        this.speechToTextService = speechToTextService;
        this.sessionManagerService = sessionManagerService;
        this.multimodalService = multimodalService;
        this.documentGeneratorService = documentGeneratorService;
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

    // 基于会话的聊天方法，支持上下文理解
    public String chatWithSession(String sessionId, String message) {
        ChatSession session = sessionManagerService.getSession(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found");
        }

        // 添加用户消息到会话
        session.addMessage("user", message);

        // 构建包含历史消息的提示
        StringBuilder promptBuilder = new StringBuilder();
        for (com.lowic.ai.entity.ChatMessage msg : session.getMessages()) {
            if (msg.getRole().equals("user")) {
                promptBuilder.append("用户：").append(msg.getContent()).append("\n");
            } else if (msg.getRole().equals("assistant")) {
                promptBuilder.append("助手：").append(msg.getContent()).append("\n");
            }
        }
        promptBuilder.append("用户：").append(message).append("\n助手：");

        // 调用模型获取回复
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        String response = chatClient.prompt()
                .user(promptBuilder.toString())
                .call()
                .content();

        // 添加模型回复到会话
        session.addMessage("assistant", response);
        sessionManagerService.saveSession(session);

        return response;
    }

    // 基于会话的聊天方法，支持系统提示和上下文理解
    public String chatWithSessionAndSystemPrompt(String sessionId, String systemPrompt, String message) {
        ChatSession session = sessionManagerService.getSession(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found");
        }

        // 添加用户消息到会话
        session.addMessage("user", message);

        // 构建包含历史消息的提示
        StringBuilder promptBuilder = new StringBuilder();
        for (com.lowic.ai.entity.ChatMessage msg : session.getMessages()) {
            if (msg.getRole().equals("user")) {
                promptBuilder.append("用户：").append(msg.getContent()).append("\n");
            } else if (msg.getRole().equals("assistant")) {
                promptBuilder.append("助手：").append(msg.getContent()).append("\n");
            }
        }
        promptBuilder.append("用户：").append(message).append("\n助手：");

        // 调用模型获取回复
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(promptBuilder.toString())
                .call()
                .content();

        // 添加模型回复到会话
        session.addMessage("assistant", response);
        sessionManagerService.saveSession(session);

        return response;
    }

    // 获取会话历史
    public List<com.lowic.ai.entity.ChatMessage> getSessionHistory(String sessionId) {
        ChatSession session = sessionManagerService.getSession(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found");
        }
        return session.getMessages();
    }

    // 创建新会话
    public ChatSession createSession(String userId) {
        return sessionManagerService.createSession(userId);
    }

    // 列出用户的所有会话
    public List<ChatSession> listSessions(String userId) {
        return sessionManagerService.listSessions(userId);
    }

    // 删除会话
    public void deleteSession(String sessionId) {
        sessionManagerService.deleteSession(sessionId);
    }

    // 基于图片的聊天方法
    public String chatWithImageFile(MultipartFile imageFile, String prompt) throws IOException {
        return multimodalService.analyzeImage(imageFile, prompt);
    }

    // 基于视频的聊天方法
    public String chatWithVideoFile(MultipartFile videoFile, String prompt) throws IOException {
        return multimodalService.analyzeVideo(videoFile, prompt);
    }

    // 基于多模态内容的聊天方法
    public String chatWithMultimodal(MultipartFile imageFile, MultipartFile videoFile, String prompt) throws IOException {
        return multimodalService.analyzeMultimodal(imageFile, videoFile, prompt);
    }

    // 带有系统提示的基于图片的聊天方法
    public String chatWithImageFileAndSystemPrompt(String systemPrompt, MultipartFile imageFile, String prompt) throws IOException {
        // 先分析图片内容
        String imageAnalysis = multimodalService.analyzeImage(imageFile, prompt);
        
        // 然后使用系统提示进行进一步处理
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        return chatClient.prompt()
                .system(systemPrompt)
                .user("图片分析结果：" + imageAnalysis)
                .call()
                .content();
    }

    // 带有系统提示的基于视频的聊天方法
    public String chatWithVideoFileAndSystemPrompt(String systemPrompt, MultipartFile videoFile, String prompt) throws IOException {
        // 先分析视频内容
        String videoAnalysis = multimodalService.analyzeVideo(videoFile, prompt);
        
        // 然后使用系统提示进行进一步处理
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        return chatClient.prompt()
                .system(systemPrompt)
                .user("视频分析结果：" + videoAnalysis)
                .call()
                .content();
    }

    // 带有系统提示的基于多模态内容的聊天方法
    public String chatWithMultimodalAndSystemPrompt(String systemPrompt, MultipartFile imageFile, MultipartFile videoFile, String prompt) throws IOException {
        // 先分析多模态内容
        String multimodalAnalysis = multimodalService.analyzeMultimodal(imageFile, videoFile, prompt);
        
        // 然后使用系统提示进行进一步处理
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        return chatClient.prompt()
                .system(systemPrompt)
                .user("多模态分析结果：" + multimodalAnalysis)
                .call()
                .content();
    }

    // 生成文档
    public String generateDocument(String documentType, String content, java.util.Map<String, Object> parameters) {
        return documentGeneratorService.generateDocument(documentType, content, parameters);
    }

    // 生成报告
    public String generateReport(String content, java.util.Map<String, Object> parameters) {
        return documentGeneratorService.generateReport(content, parameters);
    }

    // 生成邮件
    public String generateEmail(String content, java.util.Map<String, Object> parameters) {
        return documentGeneratorService.generateEmail(content, parameters);
    }

    // 生成合同
    public String generateContract(String content, java.util.Map<String, Object> parameters) {
        return documentGeneratorService.generateContract(content, parameters);
    }

    // 生成简历
    public String generateResume(String content, java.util.Map<String, Object> parameters) {
        return documentGeneratorService.generateResume(content, parameters);
    }

    // 生成会议纪要
    public String generateMeetingMinutes(String content, java.util.Map<String, Object> parameters) {
        return documentGeneratorService.generateMeetingMinutes(content, parameters);
    }

    // 生成产品需求文档
    public String generateProductRequirement(String content, java.util.Map<String, Object> parameters) {
        return documentGeneratorService.generateProductRequirement(content, parameters);
    }

    // 生成技术方案文档
    public String generateTechnicalSolution(String content, java.util.Map<String, Object> parameters) {
        return documentGeneratorService.generateTechnicalSolution(content, parameters);
    }
}

