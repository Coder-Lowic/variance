package com.lowic.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RagService {

    private final VectorStore vectorStore;
    private final ModelManagerService modelManagerService;

    @Autowired
    public RagService(VectorStore vectorStore, ModelManagerService modelManagerService) {
        this.vectorStore = vectorStore;
        this.modelManagerService = modelManagerService;
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
