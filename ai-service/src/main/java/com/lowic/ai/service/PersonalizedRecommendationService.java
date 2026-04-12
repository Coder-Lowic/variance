package com.lowic.ai.service;

import com.lowic.ai.entity.ChatSession;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class PersonalizedRecommendationService {

    private final ModelManagerService modelManagerService;
    private final SessionManagerService sessionManagerService;

    public PersonalizedRecommendationService(ModelManagerService modelManagerService, SessionManagerService sessionManagerService) {
        this.modelManagerService = modelManagerService;
        this.sessionManagerService = sessionManagerService;
    }

    /**
     * 分析用户偏好
     * @param userId 用户ID
     * @return 用户偏好分析结果
     */
    public Map<String, Object> analyzeUserPreferences(String userId) {
        // 获取用户的所有会话
        List<ChatSession> sessions = sessionManagerService.listSessions(userId);
        
        // 提取用户的所有对话历史
        StringBuilder conversationHistory = new StringBuilder();
        for (ChatSession session : sessions) {
            session.getMessages().forEach(msg -> {
                if (msg.getRole().equals("user")) {
                    conversationHistory.append("用户：").append(msg.getContent()).append("\n");
                }
            });
        }

        // 使用大模型分析用户偏好
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        String prompt = String.format("""
                请分析以下用户的对话历史，提取用户的偏好和兴趣：
                
                %s
                
                分析应包括：
                1. 用户的主要兴趣领域
                2. 用户的语言风格和沟通偏好
                3. 用户可能需要的服务或信息类型
                4. 用户的问题类型和解决需求
                5. 其他可能的用户特征
                
                请以结构化的JSON格式输出分析结果。
                """, conversationHistory.toString());

        String analysisResult = chatClient.prompt()
                .system("你是一个专业的用户行为分析助手，能够从对话历史中提取用户的偏好和兴趣。")
                .user(prompt)
                .call()
                .content();

        // 解析分析结果（这里简化处理，实际项目中需要使用JSON解析库）
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("analysis", analysisResult);
        preferences.put("sessionCount", sessions.size());
        preferences.put("totalMessages", sessions.stream().mapToInt(s -> s.getMessages().size()).sum());

        return preferences;
    }

    /**
     * 生成个性化推荐
     * @param userId 用户ID
     * @param context 当前上下文
     * @param recommendationCount 推荐数量
     * @return 个性化推荐列表
     */
    public List<String> generateRecommendations(String userId, String context, int recommendationCount) {
        // 分析用户偏好
        Map<String, Object> userPreferences = analyzeUserPreferences(userId);
        String analysisResult = (String) userPreferences.get("analysis");

        // 使用大模型生成推荐
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        String prompt = String.format("""
                基于以下用户偏好分析和当前上下文，生成%d个个性化推荐：
                
                用户偏好分析：
                %s
                
                当前上下文：
                %s
                
                推荐应包括：
                1. 与用户兴趣相关的内容
                2. 可能对用户有帮助的服务
                3. 基于用户历史行为的个性化建议
                4. 与当前上下文相关的推荐
                
                请以列表形式输出推荐，每个推荐项一行。
                """, recommendationCount, analysisResult, context);

        String recommendations = chatClient.prompt()
                .system("你是一个专业的推荐系统助手，能够基于用户偏好和当前上下文生成个性化推荐。")
                .user(prompt)
                .call()
                .content();

        // 解析推荐结果（这里简化处理，实际项目中需要更复杂的解析）
        return recommendations.lines()
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 为用户生成个性化欢迎消息
     * @param userId 用户ID
     * @return 个性化欢迎消息
     */
    public String generatePersonalizedWelcomeMessage(String userId) {
        // 分析用户偏好
        Map<String, Object> userPreferences = analyzeUserPreferences(userId);
        String analysisResult = (String) userPreferences.get("analysis");

        // 使用大模型生成欢迎消息
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        String prompt = String.format("""
                基于以下用户偏好分析，生成一条个性化的欢迎消息：
                
                用户偏好分析：
                %s
                
                欢迎消息应：
                1. 提及用户的兴趣领域
                2. 表达对用户的了解
                3. 提供与用户兴趣相关的服务建议
                4. 语气友好、个性化
                """, analysisResult);

        return chatClient.prompt()
                .system("你是一个友好的AI助手，能够基于用户偏好生成个性化的欢迎消息。")
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 为用户生成个性化内容建议
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param count 建议数量
     * @return 个性化内容建议列表
     */
    public List<String> generateContentSuggestions(String userId, String contentType, int count) {
        // 分析用户偏好
        Map<String, Object> userPreferences = analyzeUserPreferences(userId);
        String analysisResult = (String) userPreferences.get("analysis");

        // 使用大模型生成内容建议
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        String prompt = String.format("""
                基于以下用户偏好分析，生成%d个关于%s的个性化建议：
                
                用户偏好分析：
                %s
                
                建议应：
                1. 与用户兴趣相关
                2. 具体且实用
                3. 符合用户的语言风格
                4. 与%s相关
                """, count, contentType, analysisResult, contentType);

        String suggestions = chatClient.prompt()
                .system("你是一个专业的内容建议助手，能够基于用户偏好生成个性化的内容建议。")
                .user(prompt)
                .call()
                .content();

        // 解析建议结果
        return suggestions.lines()
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.toList());
    }
}
