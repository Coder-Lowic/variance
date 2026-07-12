package com.lowic.ai.service;

import com.lowic.ai.entity.DocumentCache;
import com.lowic.ai.entity.DocumentQAResult;
import com.lowic.ai.exception.DocumentNotFoundException;
import com.lowic.ai.repository.DocumentCacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DocumentQAService {
    private static final Logger log = LoggerFactory.getLogger(DocumentQAService.class);

    private final DocumentParserService documentParserService;
    private final ModelManagerService modelManagerService;
    private final RagService ragService;
    private final DocumentCacheRepository documentCacheRepository;

    public DocumentQAService(DocumentParserService documentParserService,
                             ModelManagerService modelManagerService,
                             RagService ragService,
                             DocumentCacheRepository documentCacheRepository) {
        this.documentParserService = documentParserService;
        this.modelManagerService = modelManagerService;
        this.ragService = ragService;
        this.documentCacheRepository = documentCacheRepository;
    }

    /**
     * 上传文档并持久化缓存内容
     */
    @Transactional
    public Map<String, Object> uploadDocument(MultipartFile file) throws IOException {
        String documentName = file.getOriginalFilename();
        String content = documentParserService.parseDocument(file);
        String docId = UUID.randomUUID().toString();

        DocumentCache cache = new DocumentCache(docId, documentName, content);
        documentCacheRepository.save(cache);

        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("docId", docId);
            metadata.put("fileName", documentName);
            ragService.addText(content, metadata);
        } catch (Exception e) {
            log.warn("Vector store unavailable for document {}, using DB cache only", docId);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("docId", docId);
        result.put("documentName", documentName);
        result.put("contentLength", content.length());
        result.put("preview", content.length() > 500 ? content.substring(0, 500) + "..." : content);
        return result;
    }

    /**
     * 基于上传的文档进行问答
     */
    @Transactional(readOnly = true)
    public DocumentQAResult askQuestion(String docId, String question, String sessionId) {
        String documentContent = getDocumentContent(docId);
        DocumentQAResult result = doAsk(documentContent, question, getDocumentName(docId));

        if (sessionId != null) {
            result.setSessionId(sessionId);
        }

        return result;
    }

    /**
     * 基于上传文件直接问答
     */
    @Transactional
    public DocumentQAResult askQuestionWithFile(MultipartFile file, String question, String sessionId) throws IOException {
        Map<String, Object> uploadResult = uploadDocument(file);
        String docId = (String) uploadResult.get("docId");
        return askQuestion(docId, question, sessionId);
    }

    /**
     * 直接基于文本内容问答
     */
    public DocumentQAResult askQuestionWithContent(String content, String documentName, String question, String sessionId) {
        DocumentQAResult result = doAsk(content, question, documentName);

        if (sessionId != null) {
            result.setSessionId(sessionId);
        }

        return result;
    }

    /**
     * 生成文档摘要
     */
    @Transactional(readOnly = true)
    public DocumentQAResult summarizeDocument(String docId, String summaryLength) {
        String documentContent = getDocumentContent(docId);

        String lengthDesc = switch (summaryLength != null ? summaryLength : "medium") {
            case "short" -> "请生成简短摘要，控制在100字以内";
            case "detailed" -> "请生成详细摘要，全面覆盖文档要点";
            default -> "请生成中等长度的摘要，约300-500字";
        };

        DocumentQAResult result = doAsk(documentContent,
                lengthDesc + "，并列出3-5个关键要点。",
                getDocumentName(docId));
        result.setQuestion("生成文档摘要");
        return result;
    }

    /**
     * 提取文档关键信息
     */
    @Transactional(readOnly = true)
    public DocumentQAResult extractKeyPoints(String docId) {
        String documentContent = getDocumentContent(docId);

        DocumentQAResult result = doAsk(documentContent,
                "请提取文档的关键信息，包括：主题、核心观点、重要数据、结论和建议。请以结构化格式输出。",
                getDocumentName(docId));
        result.setQuestion("提取关键信息");
        return result;
    }

    /**
     * 对比两个文档
     */
    @Transactional(readOnly = true)
    public DocumentQAResult compareDocuments(String docId1, String docId2) {
        String content1 = getDocumentContent(docId1);
        String content2 = getDocumentContent(docId2);

        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        String prompt = String.format("""
                请对比分析以下两个文档，从以下维度进行对比：
                1. 主题与核心观点
                2. 结构与组织方式
                3. 关键数据与事实
                4. 相似之处
                5. 不同之处
                6. 各自优缺点

                【文档1】
                %s

                【文档2】
                %s

                请以结构化格式输出对比结果。
                """, content1.substring(0, Math.min(8000, content1.length())),
                content2.substring(0, Math.min(8000, content2.length())));

        String answer = chatClient.prompt()
                .system("你是一个专业的文档分析专家，能够精准对比分析多个文档。")
                .user(prompt)
                .call()
                .content();

        DocumentQAResult result = new DocumentQAResult();
        result.setQuestion("文档对比分析");
        result.setAnswer(answer);
        result.setDocumentName(getDocumentName(docId1) + " vs " + getDocumentName(docId2));
        result.setTimestamp(LocalDateTime.now());
        result.setMetadata(Map.of("docId1", docId1, "docId2", docId2));
        return result;
    }

    /**
     * 基于RAG的文档问答
     */
    @Transactional(readOnly = true)
    public DocumentQAResult askQuestionWithRAG(String docId, String question, int k, String sessionId) {
        String documentContent = getDocumentContent(docId);

        String ragContext = ragService.ragQuery(question, k);
        ChatClient chatClient = modelManagerService.getCurrentChatClient();

        String prompt = String.format("""
                请基于以下文档内容和检索到的相关信息回答用户问题。

                【文档内容】
                %s

                【检索到的相关信息】
                %s

                【用户问题】
                %s

                请结合文档内容和检索信息给出准确、完整的回答。
                如果检索信息与文档内容有冲突，以文档内容为准。
                如无法从提供的内容中找到答案，请明确说明。
                """, documentContent.substring(0, Math.min(10000, documentContent.length())),
                ragContext, question);

        String answer = chatClient.prompt()
                .system("你是一个专业的文档问答助手，基于文档内容和检索增强信息回答问题。")
                .user(prompt)
                .call()
                .content();

        DocumentQAResult result = new DocumentQAResult();
        result.setQuestion(question);
        result.setAnswer(answer);
        result.setDocumentName(getDocumentName(docId));
        result.setTimestamp(LocalDateTime.now());
        result.setMetadata(Map.of("docId", docId, "mode", "RAG", "k", k));

        if (sessionId != null) {
            result.setSessionId(sessionId);
        }

        return result;
    }

    /**
     * 获取会话历史（从DB中查询该session相关的文档）
     */
    public List<DocumentQAResult> getSessionHistory(String sessionId) {
        // QA history is ephemeral — return empty list for now;
        // full history tracking can be added with a dedicated JPA entity later
        return Collections.emptyList();
    }

    /**
     * 清理会话历史
     */
    public void clearSessionHistory(String sessionId) {
        // no persistent history to clear currently
    }

    /**
     * 删除缓存的文档
     */
    @Transactional
    public void removeDocument(String docId) {
        documentCacheRepository.deleteById(docId);
        log.debug("Removed cached document {}", docId);
    }

    /**
     * 获取缓存的文档名称
     */
    @Transactional(readOnly = true)
    public String getDocumentName(String docId) {
        return documentCacheRepository.findById(docId)
                .map(DocumentCache::getFileName)
                .orElse("Document-" + docId.substring(0, Math.min(8, docId.length())));
    }

    // === private helpers ===

    private String getDocumentContent(String docId) {
        return documentCacheRepository.findById(docId)
                .map(DocumentCache::getContent)
                .orElseThrow(() -> new DocumentNotFoundException(docId));
    }

    private DocumentQAResult doAsk(String documentContent, String question, String documentName) {
        ChatClient chatClient = modelManagerService.getCurrentChatClient();

        int maxContentLength = 12000;
        String truncatedContent = documentContent.length() > maxContentLength
                ? documentContent.substring(0, maxContentLength) + "\n...(内容已截断)"
                : documentContent;

        String prompt = String.format("""
                请基于以下文档内容回答用户的问题。

                【文档内容】
                %s

                【用户问题】
                %s

                要求：
                1. 回答应基于文档内容，准确、清晰
                2. 如果文档中有相关数据，请引用
                3. 如果无法从文档中找到答案，请明确说明
                4. 可以适当补充背景知识，但要区分哪些来自文档、哪些来自你的知识
                """, truncatedContent, question);

        String answer = chatClient.prompt()
                .system("你是一个专业的文档问答助手，能够精准理解文档内容并回答用户问题。回答要基于文档事实，不编造信息。")
                .user(prompt)
                .call()
                .content();

        List<String> snippets = extractRelevantSnippets(documentContent, question, 3);

        DocumentQAResult result = new DocumentQAResult();
        result.setQuestion(question);
        result.setAnswer(answer);
        result.setDocumentName(documentName);
        result.setRelevantSnippets(snippets);
        result.setTimestamp(LocalDateTime.now());
        result.setMetadata(Map.of("contentLength", documentContent.length()));

        return result;
    }

    /**
     * 简单的关键词匹配提取相关片段
     */
    private List<String> extractRelevantSnippets(String content, String question, int count) {
        List<String> lines = Arrays.asList(content.split("\n"));
        String[] keywords = question.split("\\s+");

        Map<String, Integer> scoredLines = new LinkedHashMap<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.length() < 10) continue;
            int score = 0;
            for (String keyword : keywords) {
                if (keyword.length() > 1 && trimmed.toLowerCase().contains(keyword.toLowerCase())) {
                    score++;
                }
            }
            if (score > 0) {
                scoredLines.put(trimmed, score);
            }
        }

        return scoredLines.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(count)
                .map(Map.Entry::getKey)
                .toList();
    }
}
