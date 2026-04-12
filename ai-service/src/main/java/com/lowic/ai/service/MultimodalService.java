package com.lowic.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class MultimodalService {

    private final ModelManagerService modelManagerService;

    public MultimodalService(ModelManagerService modelManagerService) {
        this.modelManagerService = modelManagerService;
    }

    /**
     * 分析图片内容
     * @param imageFile 图片文件
     * @param prompt 分析提示
     * @return 分析结果
     * @throws IOException 处理图片文件时可能发生的异常
     */
    public String analyzeImage(MultipartFile imageFile, String prompt) throws IOException {
        // 将图片转换为Base64编码
        byte[] imageBytes = imageFile.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String imageData = "data:" + imageFile.getContentType() + ";base64," + base64Image;

        // 创建用户消息，包含提示和图片
        String userPrompt = String.format("""
                %s
                
                [Image]
                %s
                """, prompt, imageData);

        // 调用多模态模型
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        return chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();
    }

    /**
     * 分析视频内容
     * @param videoFile 视频文件
     * @param prompt 分析提示
     * @return 分析结果
     * @throws IOException 处理视频文件时可能发生的异常
     */
    public String analyzeVideo(MultipartFile videoFile, String prompt) throws IOException {
        // 将视频转换为Base64编码
        byte[] videoBytes = videoFile.getBytes();
        String base64Video = Base64.getEncoder().encodeToString(videoBytes);
        String videoData = "data:" + videoFile.getContentType() + ";base64," + base64Video;

        // 创建用户消息，包含提示和视频
        String userPrompt = String.format("""
                %s
                
                [Video]
                %s
                """, prompt, videoData);

        // 调用多模态模型
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        return chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();
    }

    /**
     * 分析多模态内容（图片和视频组合）
     * @param imageFile 图片文件
     * @param videoFile 视频文件
     * @param prompt 分析提示
     * @return 分析结果
     * @throws IOException 处理文件时可能发生的异常
     */
    public String analyzeMultimodal(MultipartFile imageFile, MultipartFile videoFile, String prompt) throws IOException {
        StringBuilder userPrompt = new StringBuilder();
        userPrompt.append(prompt).append("\n\n");

        // 添加图片
        if (imageFile != null && !imageFile.isEmpty()) {
            byte[] imageBytes = imageFile.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String imageData = "data:" + imageFile.getContentType() + ";base64," + base64Image;
            userPrompt.append("[Image]\n").append(imageData).append("\n\n");
        }

        // 添加视频
        if (videoFile != null && !videoFile.isEmpty()) {
            byte[] videoBytes = videoFile.getBytes();
            String base64Video = Base64.getEncoder().encodeToString(videoBytes);
            String videoData = "data:" + videoFile.getContentType() + ";base64," + base64Video;
            userPrompt.append("[Video]\n").append(videoData).append("\n\n");
        }

        // 调用多模态模型
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        return chatClient.prompt()
                .user(userPrompt.toString())
                .call()
                .content();
    }
}
