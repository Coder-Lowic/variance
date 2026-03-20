package com.lowic.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final ModelManagerService modelManagerService;
    private final VectorStore vectorStore;

    public RagService(ModelManagerService modelManagerService, VectorStore vectorStore) {
        this.modelManagerService = modelManagerService;
        this.vectorStore = vectorStore;
    }

    public void addDocuments(List<Document> documents) {
        vectorStore.add(documents);
    }

    public String ragQuery(String query, int topK) {
        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.query(query).withTopK(topK)
        );

        String context = similarDocuments.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n\n"));

        String prompt = String.format("""
                请基于以下上下文回答用户的问题。如果上下文没有相关信息，请坦诚地说不知道。
                
                上下文：
                %s
                
                用户问题：%s
                """, context, query);

        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
