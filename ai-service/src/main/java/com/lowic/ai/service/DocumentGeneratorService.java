package com.lowic.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DocumentGeneratorService {

    private final ModelManagerService modelManagerService;

    public DocumentGeneratorService(ModelManagerService modelManagerService) {
        this.modelManagerService = modelManagerService;
    }

    /**
     * 生成文档
     * @param documentType 文档类型
     * @param content 文档内容
     * @param parameters 文档参数
     * @return 生成的文档
     */
    public String generateDocument(String documentType, String content, Map<String, Object> parameters) {
        ChatClient chatClient = modelManagerService.getCurrentChatClient();
        String prompt = buildDocumentPrompt(documentType, content, parameters);

        return chatClient.prompt()
                .system("你是一个专业的文档生成助手，能够生成各种类型的文档，包括报告、邮件、合同、简历等。")
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 生成报告
     * @param content 报告内容
     * @param parameters 报告参数
     * @return 生成的报告
     */
    public String generateReport(String content, Map<String, Object> parameters) {
        return generateDocument("report", content, parameters);
    }

    /**
     * 生成邮件
     * @param content 邮件内容
     * @param parameters 邮件参数
     * @return 生成的邮件
     */
    public String generateEmail(String content, Map<String, Object> parameters) {
        return generateDocument("email", content, parameters);
    }

    /**
     * 生成合同
     * @param content 合同内容
     * @param parameters 合同参数
     * @return 生成的合同
     */
    public String generateContract(String content, Map<String, Object> parameters) {
        return generateDocument("contract", content, parameters);
    }

    /**
     * 生成简历
     * @param content 简历内容
     * @param parameters 简历参数
     * @return 生成的简历
     */
    public String generateResume(String content, Map<String, Object> parameters) {
        return generateDocument("resume", content, parameters);
    }

    /**
     * 生成会议纪要
     * @param content 会议内容
     * @param parameters 会议纪要参数
     * @return 生成的会议纪要
     */
    public String generateMeetingMinutes(String content, Map<String, Object> parameters) {
        return generateDocument("meeting_minutes", content, parameters);
    }

    /**
     * 生成产品需求文档
     * @param content 产品需求内容
     * @param parameters 产品需求文档参数
     * @return 生成的产品需求文档
     */
    public String generateProductRequirement(String content, Map<String, Object> parameters) {
        return generateDocument("product_requirement", content, parameters);
    }

    /**
     * 生成技术方案文档
     * @param content 技术方案内容
     * @param parameters 技术方案文档参数
     * @return 生成的技术方案文档
     */
    public String generateTechnicalSolution(String content, Map<String, Object> parameters) {
        return generateDocument("technical_solution", content, parameters);
    }

    /**
     * 构建文档生成提示
     * @param documentType 文档类型
     * @param content 文档内容
     * @param parameters 文档参数
     * @return 构建的提示
     */
    private String buildDocumentPrompt(String documentType, String content, Map<String, Object> parameters) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }

        StringBuilder promptBuilder = new StringBuilder();

        switch (documentType) {
            case "report":
                promptBuilder.append("请生成一份详细的报告，内容如下：\n\n");
                promptBuilder.append(content).append("\n\n");
                promptBuilder.append("报告应包括：\n");
                promptBuilder.append("1. 摘要\n");
                promptBuilder.append("2. 背景\n");
                promptBuilder.append("3. 详细内容\n");
                promptBuilder.append("4. 结论\n");
                promptBuilder.append("5. 建议\n");
                break;
            case "email":
                promptBuilder.append("请生成一封邮件，内容如下：\n\n");
                promptBuilder.append(content).append("\n\n");
                promptBuilder.append("邮件应包括：\n");
                promptBuilder.append("1. 主题\n");
                promptBuilder.append("2. 称呼\n");
                promptBuilder.append("3. 正文\n");
                promptBuilder.append("4. 结尾\n");
                promptBuilder.append("5. 签名\n");
                break;
            case "contract":
                promptBuilder.append("请生成一份合同，内容如下：\n\n");
                promptBuilder.append(content).append("\n\n");
                promptBuilder.append("合同应包括：\n");
                promptBuilder.append("1. 合同标题\n");
                promptBuilder.append("2. 双方信息\n");
                promptBuilder.append("3. 条款\n");
                promptBuilder.append("4. 权利和义务\n");
                promptBuilder.append("5. 违约责任\n");
                promptBuilder.append("6. 争议解决\n");
                promptBuilder.append("7. 生效条件\n");
                promptBuilder.append("8. 双方签字\n");
                break;
            case "resume":
                promptBuilder.append("请生成一份简历，内容如下：\n\n");
                promptBuilder.append(content).append("\n\n");
                promptBuilder.append("简历应包括：\n");
                promptBuilder.append("1. 个人信息\n");
                promptBuilder.append("2. 教育背景\n");
                promptBuilder.append("3. 工作经历\n");
                promptBuilder.append("4. 项目经验\n");
                promptBuilder.append("5. 技能\n");
                promptBuilder.append("6. 证书\n");
                promptBuilder.append("7. 自我评价\n");
                break;
            case "meeting_minutes":
                promptBuilder.append("请生成一份会议纪要，内容如下：\n\n");
                promptBuilder.append(content).append("\n\n");
                promptBuilder.append("会议纪要应包括：\n");
                promptBuilder.append("1. 会议基本信息\n");
                promptBuilder.append("2. 参会人员\n");
                promptBuilder.append("3. 会议议程\n");
                promptBuilder.append("4. 讨论内容\n");
                promptBuilder.append("5. 共识\n");
                promptBuilder.append("6. 行动项\n");
                promptBuilder.append("7. 下次会议安排\n");
                break;
            case "product_requirement":
                promptBuilder.append("请生成一份产品需求文档，内容如下：\n\n");
                promptBuilder.append(content).append("\n\n");
                promptBuilder.append("产品需求文档应包括：\n");
                promptBuilder.append("1. 产品概述\n");
                promptBuilder.append("2. 核心功能\n");
                promptBuilder.append("3. 用户流程\n");
                promptBuilder.append("4. 界面设计\n");
                promptBuilder.append("5. 技术要求\n");
                promptBuilder.append("6. 验收标准\n");
                break;
            case "technical_solution":
                promptBuilder.append("请生成一份技术方案文档，内容如下：\n\n");
                promptBuilder.append(content).append("\n\n");
                promptBuilder.append("技术方案文档应包括：\n");
                promptBuilder.append("1. 技术选型\n");
                promptBuilder.append("2. 架构设计\n");
                promptBuilder.append("3. 模块划分\n");
                promptBuilder.append("4. 关键技术点\n");
                promptBuilder.append("5. 部署方案\n");
                promptBuilder.append("6. 性能优化\n");
                break;
            default:
                promptBuilder.append("请生成一份文档，内容如下：\n\n");
                promptBuilder.append(content).append("\n\n");
                promptBuilder.append("文档应结构清晰，内容完整。\n");
                break;
        }

        // 添加参数信息
        if (!parameters.isEmpty()) {
            promptBuilder.append("\n文档参数：\n");
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                promptBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        promptBuilder.append("\n请生成一份格式规范、内容完整的文档。");

        return promptBuilder.toString();
    }
}
