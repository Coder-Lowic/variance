package com.lowic.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class RagService {

    private final VectorStore vectorStore;
    private final ModelManagerService modelManagerService;
    private final MultimodalService multimodalService;

    @Autowired
    public RagService(VectorStore vectorStore, ModelManagerService modelManagerService, MultimodalService multimodalService) {
        this.vectorStore = vectorStore;
        this.modelManagerService = modelManagerService;
        this.multimodalService = multimodalService;
    }

    /**
     * 向向量存储中添加文本
     * @param text 文本内容
     * @param metadata 文本元数据
     * @return 添加的文档数量
     */
    public int addText(String text, Map<String, Object> metadata) {
        Document document = new Document(text, metadata);
        vectorStore.add(List.of(document));
        return 1;
    }

    /**
     * 向向量存储中添加图像
     * @param imageFile 图像文件
     * @param metadata 图像元数据
     * @return 添加的文档数量
     * @throws IOException 处理图像文件时可能发生的异常
     */
    public int addImage(MultipartFile imageFile, Map<String, Object> metadata) throws IOException {
        // 生成图像描述
        String imageDescription = multimodalService.analyzeImage(imageFile, "请详细描述这张图片的内容，包括物体、场景、颜色、人物等所有可见元素");
        
        // 将图像转换为Base64编码
        byte[] imageBytes = imageFile.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String imageData = "data:" + imageFile.getContentType() + ";base64," + base64Image;
        
        // 构建文档内容
        String content = String.format("""
                [Image Description]
                %s
                
                [Image Data]
                %s
                """, imageDescription, imageData);
        
        // 添加元数据
        if (metadata == null) {
            metadata = new java.util.HashMap<>();
        }
        metadata.put("type", "image");
        metadata.put("filename", imageFile.getOriginalFilename());
        metadata.put("contentType", imageFile.getContentType());
        metadata.put("size", imageFile.getSize());
        
        // 创建文档并存储
        Document document = new Document(content, metadata);
        vectorStore.add(List.of(document));
        return 1;
    }

    /**
     * 向向量存储中添加视频
     * @param videoFile 视频文件
     * @param metadata 视频元数据
     * @return 添加的文档数量
     * @throws IOException 处理视频文件时可能发生的异常
     */
    public int addVideo(MultipartFile videoFile, Map<String, Object> metadata) throws IOException {
        // 生成视频描述
        String videoDescription = multimodalService.analyzeVideo(videoFile, "请详细描述这个视频的内容，包括场景、人物、动作、对话等所有关键元素");
        
        // 将视频转换为Base64编码
        byte[] videoBytes = videoFile.getBytes();
        String base64Video = Base64.getEncoder().encodeToString(videoBytes);
        String videoData = "data:" + videoFile.getContentType() + ";base64," + base64Video;
        
        // 构建文档内容
        String content = String.format("""
                [Video Description]
                %s
                
                [Video Data]
                %s
                """, videoDescription, videoData);
        
        // 添加元数据
        if (metadata == null) {
            metadata = new java.util.HashMap<>();
        }
        metadata.put("type", "video");
        metadata.put("filename", videoFile.getOriginalFilename());
        metadata.put("contentType", videoFile.getContentType());
        metadata.put("size", videoFile.getSize());
        
        // 创建文档并存储
        Document document = new Document(content, metadata);
        vectorStore.add(List.of(document));
        return 1;
    }

    /**
     * 从向量存储中删除文档
     * @param documentId 文档ID
     * @return 是否删除成功
     */
    public boolean deleteDocument(String documentId) {
        vectorStore.delete(List.of(documentId));
        return true;
    }

    /**
     * 基于RAG的查询
     * @param query 查询文本
     * @param k 检索的文档数量
     * @return 查询结果
     */
    public String ragQuery(String query, int k) {
        // 从向量存储中检索相关文档
        List<Document> relevantDocuments = vectorStore.similaritySearch(query);

        // 限制文档数量
        if (relevantDocuments.size() > k) {
            relevantDocuments = relevantDocuments.subList(0, k);
        }

        // 构建上下文
        StringBuilder context = new StringBuilder();
        for (Document doc : relevantDocuments) {
            context.append("Document: " + doc.getId() + "\n");
            context.append("Content: " + doc.getContent() + "\n\n");
        }

        // 构建提示
        String prompt = String.format("""
                请基于以下上下文回答用户的问题：
                
                %s
                
                用户问题：%s
                
                请根据上下文内容回答，不要使用上下文之外的信息。
                """, context.toString(), query);

        // 调用大模型
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        return chatClient.prompt()
                .system("你是一个基于检索增强生成（RAG）的AI助手，能够基于提供的上下文回答用户的问题。")
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 基于图像的RAG查询
     * @param imageFile 图像文件
     * @param prompt 查询提示
     * @param k 检索的文档数量
     * @return 查询结果
     * @throws IOException 处理图像文件时可能发生的异常
     */
    public String ragQueryWithImage(MultipartFile imageFile, String prompt, int k) throws IOException {
        // 生成图像描述
        String imageDescription = multimodalService.analyzeImage(imageFile, "请详细描述这张图片的内容，包括物体、场景、颜色、人物等所有可见元素");
        
        // 从向量存储中检索相关文档
        List<Document> relevantDocuments = vectorStore.similaritySearch(imageDescription);

        // 限制文档数量
        if (relevantDocuments.size() > k) {
            relevantDocuments = relevantDocuments.subList(0, k);
        }

        // 构建上下文
        StringBuilder context = new StringBuilder();
        for (Document doc : relevantDocuments) {
            context.append("Document: " + doc.getId() + "\n");
            context.append("Content: " + doc.getContent() + "\n\n");
        }

        // 将图像转换为Base64编码
        byte[] imageBytes = imageFile.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String imageData = "data:" + imageFile.getContentType() + ";base64," + base64Image;

        // 构建提示
        String userPrompt = String.format("""
                请基于以下上下文和图片回答用户的问题：
                
                %s
                
                [Image]
                %s
                
                用户问题：%s
                
                请根据上下文内容和图片信息回答，不要使用上下文之外的信息。
                """, context.toString(), imageData, prompt);

        // 调用大模型
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        return chatClient.prompt()
                .system("你是一个基于检索增强生成（RAG）的AI助手，能够基于提供的上下文和图片回答用户的问题。")
                .user(userPrompt)
                .call()
                .content();
    }

    /**
     * 基于RAG的查询（带系统提示）
     * @param systemPrompt 系统提示
     * @param query 查询文本
     * @param k 检索的文档数量
     * @return 查询结果
     */
    public String ragQueryWithSystemPrompt(String systemPrompt, String query, int k) {
        // 从向量存储中检索相关文档
        List<Document> relevantDocuments = vectorStore.similaritySearch(query);

        // 限制文档数量
        if (relevantDocuments.size() > k) {
            relevantDocuments = relevantDocuments.subList(0, k);
        }

        // 构建上下文
        StringBuilder context = new StringBuilder();
        for (Document doc : relevantDocuments) {
            context.append("Document: " + doc.getId() + "\n");
            context.append("Content: " + doc.getContent() + "\n\n");
        }

        // 构建提示
        String prompt = String.format("""
                请基于以下上下文回答用户的问题：
                
                %s
                
                用户问题：%s
                
                请根据上下文内容回答，不要使用上下文之外的信息。
                """, context.toString(), query);

        // 调用大模型
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        return chatClient.prompt()
                .system(systemPrompt)
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 基于RAG的会话查询
     * @param sessionId 会话ID
     * @param query 查询文本
     * @param k 检索的文档数量
     * @return 查询结果
     */
    public String ragQueryWithSession(String sessionId, String query, int k) {
        // 从向量存储中检索相关文档
        List<Document> relevantDocuments = vectorStore.similaritySearch(query);

        // 限制文档数量
        if (relevantDocuments.size() > k) {
            relevantDocuments = relevantDocuments.subList(0, k);
        }

        // 构建上下文
        StringBuilder context = new StringBuilder();
        for (Document doc : relevantDocuments) {
            context.append("Document: " + doc.getId() + "\n");
            context.append("Content: " + doc.getContent() + "\n\n");
        }

        // 构建提示
        String prompt = String.format("""
                请基于以下上下文回答用户的问题：
                
                %s
                
                用户问题：%s
                
                请根据上下文内容回答，不要使用上下文之外的信息。
                """, context.toString(), query);

        // 调用大模型
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        return chatClient.prompt()
                .system("你是一个基于检索增强生成（RAG）的AI助手，能够基于提供的上下文回答用户的问题。")
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 搜索向量存储中的文档
     * @param query 查询文本
     * @param k 检索的文档数量
     * @return 相关文档列表
     */
    public List<Document> searchDocuments(String query, int k) {
        List<Document> relevantDocuments = vectorStore.similaritySearch(query);
        // 限制文档数量
        if (relevantDocuments.size() > k) {
            relevantDocuments = relevantDocuments.subList(0, k);
        }
        return relevantDocuments;
    }
}
