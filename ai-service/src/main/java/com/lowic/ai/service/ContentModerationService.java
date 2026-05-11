package com.lowic.ai.service;

import com.lowic.ai.entity.ContentModerationResult;
import com.lowic.ai.entity.ModerationCategory;
import com.lowic.ai.entity.ModerationRiskLevel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ContentModerationService {

    private final ModelManagerService modelManagerService;
    private final MultimodalService multimodalService;

    @Autowired
    public ContentModerationService(ModelManagerService modelManagerService, MultimodalService multimodalService) {
        this.modelManagerService = modelManagerService;
        this.multimodalService = multimodalService;
    }

    public ContentModerationResult moderateText(String content, String contentId) {
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        
        String prompt = String.format("""
                请对以下内容进行全面的内容审核分析：
                
                %s
                
                请从以下维度进行审核：
                1. 暴力内容
                2. 仇恨言论
                3. 色情内容
                4. 骚扰内容
                5. 自我伤害
                6. 非法内容
                7. 敏感信息泄露
                8. 虚假信息
                9. 垃圾广告
                
                对每个维度，给出0-1之间的风险评分（0为安全，1为高危），并判断是否需要标记。
                最后给出总体风险等级（low/medium/high/very_high）和综合判断（是否安全）。
                
                请以JSON格式返回结果，结构如下：
                {
                    "isSafe": boolean,
                    "overallRiskLevel": "low/medium/high/very_high",
                    "categories": [
                        {
                            "categoryName": "类别名称",
                            "flagged": boolean,
                            "score": 0.0-1.0,
                            "severityLevel": "low/medium/high/very_high"
                        }
                    ],
                    "details": {
                        "summary": "审核摘要",
                        "suggestions": ["建议1", "建议2"]
                    }
                }
                """, content);

        String response = chatClient.prompt()
                .system("你是一个专业的内容审核员，能够准确识别和评估各类违规内容。")
                .user(prompt)
                .call()
                .content();

        return parseModerationResult(response, content, contentId, "AI Moderation");
    }

    public ContentModerationResult moderateImage(MultipartFile imageFile, String contentId) throws Exception {
        String imageAnalysis = multimodalService.analyzeImage(imageFile, 
                "请详细描述这张图片的内容，特别注意是否包含暴力、色情、敏感信息等违规内容");

        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        
        String prompt = String.format("""
                基于以下图片描述进行内容审核：
                
                %s
                
                请从以下维度进行审核：
                1. 暴力/血腥内容
                2. 色情/成人内容
                3. 敏感信息泄露
                4. 非法内容
                5. 仇恨内容
                
                对每个维度，给出0-1之间的风险评分（0为安全，1为高危），并判断是否需要标记。
                最后给出总体风险等级（low/medium/high/very_high）和综合判断（是否安全）。
                
                请以JSON格式返回结果。
                """, imageAnalysis);

        String response = chatClient.prompt()
                .system("你是一个专业的图片内容审核员。")
                .user(prompt)
                .call()
                .content();

        ContentModerationResult result = parseModerationResult(response, imageAnalysis, contentId, "AI Image Moderation");
        Map<String, Object> details = result.getDetails();
        details.put("imageDescription", imageAnalysis);
        details.put("fileName", imageFile.getOriginalFilename());
        result.setDetails(details);
        
        return result;
    }

    public ContentModerationResult moderateVideo(MultipartFile videoFile, String contentId) throws Exception {
        String videoAnalysis = multimodalService.analyzeVideo(videoFile,
                "请详细描述这个视频的内容，特别注意是否包含暴力、色情、敏感信息等违规内容");

        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        
        String prompt = String.format("""
                基于以下视频描述进行内容审核：
                
                %s
                
                请从以下维度进行审核：
                1. 暴力/血腥内容
                2. 色情/成人内容
                3. 敏感信息泄露
                4. 非法内容
                5. 仇恨内容
                
                对每个维度，给出0-1之间的风险评分（0为安全，1为高危），并判断是否需要标记。
                最后给出总体风险等级（low/medium/high/very_high）和综合判断（是否安全）。
                
                请以JSON格式返回结果。
                """, videoAnalysis);

        String response = chatClient.prompt()
                .system("你是一个专业的视频内容审核员。")
                .user(prompt)
                .call()
                .content();

        ContentModerationResult result = parseModerationResult(response, videoAnalysis, contentId, "AI Video Moderation");
        Map<String, Object> details = result.getDetails();
        details.put("videoDescription", videoAnalysis);
        details.put("fileName", videoFile.getOriginalFilename());
        result.setDetails(details);
        
        return result;
    }

    public ContentModerationResult filterSensitiveInfo(String content, String contentId) {
        String filteredContent = content;
        Map<String, List<String>> detectedInfo = new HashMap<>();

        Pattern phonePattern = Pattern.compile("1[3-9]\\d{9}");
        Pattern emailPattern = Pattern.compile("\\w+@\\w+\\.\\w+");
        Pattern idCardPattern = Pattern.compile("\\d{17}[\\dXx]");
        Pattern bankCardPattern = Pattern.compile("\\d{16,19}");

        java.util.regex.Matcher matcher = phonePattern.matcher(content);
        List<String> phones = new ArrayList<>();
        while (matcher.find()) {
            phones.add(matcher.group());
            filteredContent = filteredContent.replace(matcher.group(), "****");
        }
        if (!phones.isEmpty()) detectedInfo.put("phones", phones);

        matcher = emailPattern.matcher(content);
        List<String> emails = new ArrayList<>();
        while (matcher.find()) {
            emails.add(matcher.group());
            filteredContent = filteredContent.replace(matcher.group(), "***@***");
        }
        if (!emails.isEmpty()) detectedInfo.put("emails", emails);

        matcher = idCardPattern.matcher(content);
        List<String> idCards = new ArrayList<>();
        while (matcher.find()) {
            idCards.add(matcher.group());
            filteredContent = filteredContent.replace(matcher.group(), "****************");
        }
        if (!idCards.isEmpty()) detectedInfo.put("idCards", idCards);

        List<ModerationCategory> categories = new ArrayList<>();
        boolean hasSensitiveInfo = !detectedInfo.isEmpty();
        categories.add(new ModerationCategory("sensitive_info", hasSensitiveInfo, 
                hasSensitiveInfo ? 0.7 : 0.0, hasSensitiveInfo ? "high" : "low"));

        Map<String, Object> details = new HashMap<>();
        details.put("filteredContent", filteredContent);
        details.put("detectedInfo", detectedInfo);
        details.put("summary", hasSensitiveInfo ? "检测到敏感信息" : "未检测到敏感信息");

        return new ContentModerationResult(
                contentId,
                content,
                !hasSensitiveInfo,
                hasSensitiveInfo ? "high" : "low",
                categories,
                details,
                LocalDateTime.now(),
                "Sensitive Info Filter"
        );
    }

    public List<ContentModerationResult> batchModerate(List<String> contents) {
        List<ContentModerationResult> results = new ArrayList<>();
        for (int i = 0; i < contents.size(); i++) {
            results.add(moderateText(contents.get(i), "batch_" + i));
        }
        return results;
    }

    public ContentModerationResult comprehensiveModeration(String content, String contentId, 
                                                              boolean checkSensitiveInfo, boolean checkAI, 
                                                              boolean checkImage, MultipartFile image) throws Exception {
        List<ContentModerationResult> moderationResults = new ArrayList<>();

        if (checkAI) {
            moderationResults.add(moderateText(content, contentId + "_ai"));
        }

        if (checkSensitiveInfo) {
            moderationResults.add(filterSensitiveInfo(content, contentId + "_sensitive"));
        }

        if (checkImage && image != null) {
            moderationResults.add(moderateImage(image, contentId + "_image"));
        }

        ContentModerationResult finalResult = new ContentModerationResult();
        finalResult.setContentId(contentId);
        finalResult.setOriginalContent(content);
        finalResult.setModerationTime(LocalDateTime.now());
        finalResult.setModeratedBy("Comprehensive Moderation");

        boolean overallSafe = moderationResults.stream().allMatch(ContentModerationResult::isSafe);
        finalResult.setSafe(overallSafe);

        String highestRisk = ModerationRiskLevel.LOW.getValue();
        for (ContentModerationResult result : moderationResults) {
            String risk = result.getOverallRiskLevel();
            if (riskLevelToInt(risk) > riskLevelToInt(highestRisk)) {
                highestRisk = risk;
            }
        }
        finalResult.setOverallRiskLevel(highestRisk);

        List<ModerationCategory> allCategories = new ArrayList<>();
        Map<String, Object> allDetails = new HashMap<>();
        allDetails.put("moderationResults", moderationResults);

        for (ContentModerationResult result : moderationResults) {
            if (result.getCategories() != null) {
                allCategories.addAll(result.getCategories());
            }
            if (result.getDetails() != null) {
                allDetails.putAll(result.getDetails());
            }
        }

        finalResult.setCategories(allCategories);
        finalResult.setDetails(allDetails);

        return finalResult;
    }

    private ContentModerationResult parseModerationResult(String aiResponse, String originalContent, 
                                                          String contentId, String moderatedBy) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> responseMap = mapper.readValue(aiResponse, Map.class);

            boolean isSafe = (boolean) responseMap.getOrDefault("isSafe", true);
            String overallRiskLevel = (String) responseMap.getOrDefault("overallRiskLevel", "low");
            List<ModerationCategory> categories = new ArrayList<>();

            List<Map<String, Object>> categoriesList = (List<Map<String, Object>>) responseMap.get("categories");
            if (categoriesList != null) {
                for (Map<String, Object> cat : categoriesList) {
                    ModerationCategory category = new ModerationCategory();
                    category.setCategoryName((String) cat.getOrDefault("categoryName", ""));
                    category.setFlagged((boolean) cat.getOrDefault("flagged", false));
                    category.setScore(((Number) cat.getOrDefault("score", 0.0)).doubleValue());
                    category.setSeverityLevel((String) cat.getOrDefault("severityLevel", "low"));
                    categories.add(category);
                }
            }

            Map<String, Object> details = (Map<String, Object>) responseMap.getOrDefault("details", new HashMap<>());

            return new ContentModerationResult(
                    contentId,
                    originalContent,
                    isSafe,
                    overallRiskLevel,
                    categories,
                    details,
                    LocalDateTime.now(),
                    moderatedBy
            );
        } catch (Exception e) {
            List<ModerationCategory> fallbackCategories = new ArrayList<>();
            fallbackCategories.add(new ModerationCategory("parsing_error", false, 0.0, "low"));
            Map<String, Object> fallbackDetails = new HashMap<>();
            fallbackDetails.put("error", "解析AI响应失败: " + e.getMessage());
            fallbackDetails.put("rawResponse", aiResponse);

            return new ContentModerationResult(
                    contentId,
                    originalContent,
                    true,
                    "low",
                    fallbackCategories,
                    fallbackDetails,
                    LocalDateTime.now(),
                    moderatedBy + " (Fallback)"
            );
        }
    }

    private int riskLevelToInt(String level) {
        switch (level.toLowerCase()) {
            case "very_high":
                return 4;
            case "high":
                return 3;
            case "medium":
                return 2;
            default:
                return 1;
        }
    }
}
