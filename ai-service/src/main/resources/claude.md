# Claude 模型配置与使用指南

## 概述

本项目集成了 Anthropic Claude 模型，提供强大的大语言模型能力。Claude 是 Anthropic 公司开发的先进AI助手，以其卓越的安全性、长上下文理解能力和多模态支持而著称。

## 配置说明

### 环境变量配置

在启动应用前，需要设置 Claude API 密钥：

```bash
export ANTHROPIC_API_KEY=your-api-key-here
```

### application.yml 配置

```yaml
spring:
  ai:
    anthropic:
      api-key: ${ANTHROPIC_API_KEY:}
      chat:
        options:
          model: claude-3-opus-20240229
          temperature: 0.7
          max-tokens: 4096
```

### 支持的模型

| 模型名称 | 模型标识 | 特点 |
|---------|---------|------|
| Claude 3 Opus | claude-3-opus-20240229 | 最强性能，适合复杂任务 |
| Claude 3 Sonnet | claude-3-sonnet-20240229 | 平衡性能与速度 |
| Claude 3 Haiku | claude-3-haiku-20240307 | 最快响应，适合简单任务 |

## 使用方式

### 1. 基础对话

```java
@Autowired
private ModelManagerService modelManagerService;

public String chatWithClaude(String message) {
    // 切换到 Claude 模型
    modelManagerService.setCurrentProvider(ModelProvider.CLAUDE);
    
    ChatClient chatClient = modelManagerService.getCurrentChatClient();
    return chatClient.prompt()
            .system("你是一个专业的AI助手")
            .user(message)
            .call()
            .content();
}
```

### 2. 文档生成

```java
@Autowired
private DocumentGeneratorService documentGeneratorService;

public String generateReport(String content) {
    // 切换到 Claude 模型
    modelManagerService.setCurrentProvider(ModelProvider.CLAUDE);
    
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("format", "markdown");
    parameters.put("language", "中文");
    
    return documentGeneratorService.generateDocument("report", content, parameters);
}
```

### 3. RAG 检索增强

```java
@Autowired
private RagService ragService;

public String queryWithRAG(String question) {
    // 切换到 Claude 模型
    modelManagerService.setCurrentProvider(ModelProvider.CLAUDE);
    
    return ragService.ragQuery(question, 5);
}
```

### 4. 多模态查询

```java
@Autowired
private RagService ragService;

public String queryWithImage(MultipartFile imageFile, String prompt) throws IOException {
    // 切换到 Claude 模型
    modelManagerService.setCurrentProvider(ModelProvider.CLAUDE);
    
    return ragService.ragQueryWithImage(imageFile, prompt, 5);
}
```

### 5. 个性化推荐

```java
@Autowired
private PersonalizedRecommendationService recommendationService;

public List<String> getRecommendations(String userId, String context) {
    // 切换到 Claude 模型
    modelManagerService.setCurrentProvider(ModelProvider.CLAUDE);
    
    return recommendationService.generateRAGBasedRecommendations(userId, context, 5);
}
```

## API 接口

### 切换模型

**请求：**
```http
POST /api/chat/model
Content-Type: application/json

{
  "provider": "claude",
  "model": "claude-3-sonnet-20240229"
}
```

**响应：**
```json
{
  "success": true,
  "message": "已切换到 Claude 模型"
}
```

### 发送消息

**请求：**
```http
POST /api/chat/send
Content-Type: application/json

{
  "message": "请帮我分析这份报告",
  "useRAG": true
}
```

**响应：**
```json
{
  "success": true,
  "response": "根据您提供的报告内容，我分析如下..."
}
```

## Claude 优势特性

### 1. 超长上下文

Claude 3 Opus 支持 200K+ token 的超长上下文，适合处理：
- 长篇文档分析
- 多文档对比
- 代码库理解
- 对话历史回顾

### 2. 多模态支持

支持图像理解和分析，可处理：
- 图表分析
- 图像描述
- 文档扫描件
- 截图分析

### 3. 安全性

Claude 内置了强大的安全防护机制：
- 严格的内容过滤
- 拒绝有害请求
- 合规性保障

### 4. 代码能力

Claude 在代码生成和理解方面表现出色：
- 支持多种编程语言
- 代码审查和优化建议
- 调试和错误修复

## 最佳实践

### 提示词设计

```java
String prompt = String.format("""
    请基于以下上下文回答问题：
    
    上下文：%s
    
    问题：%s
    
    要求：
    1. 回答要详细且准确
    2. 使用中文回复
    3. 引用上下文内容时使用引号
    4. 如果不确定答案，请说明
    """, context, question);
```

### 温度参数调整

| 值 | 效果 | 适用场景 |
|---|------|---------|
| 0.1-0.3 | 确定性高，输出保守 | 事实问答、数据分析 |
| 0.5-0.7 | 平衡创造性与一致性 | 日常对话、文档生成 |
| 0.8-1.0 | 高度创造性 | 创意写作、头脑风暴 |

### 错误处理

```java
try {
    String response = chatClient.prompt()
            .user(message)
            .call()
            .content();
    return response;
} catch (Exception e) {
    log.error("Claude API 调用失败", e);
    return "抱歉，服务暂时不可用，请稍后重试";
}
```

## 性能优化

### 1. 请求批处理

对于批量任务，建议合并请求以减少API调用次数。

### 2. 缓存策略

对重复查询使用缓存，减少不必要的API调用。

### 3. 异步处理

对于耗时较长的任务，使用异步处理避免阻塞。

## 故障排除

### 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|---------|
| API 密钥无效 | 密钥错误或过期 | 检查并更新 API 密钥 |
| 请求超时 | 网络问题或模型繁忙 | 增加超时时间或重试 |
| 内容被拒绝 | 违反安全政策 | 检查请求内容 |
| 配额超限 | API 调用次数达到上限 | 等待配额重置或升级 |

### 日志排查

```java
@Autowired
private ModelManagerService modelManagerService;

// 启用详细日志
modelManagerService.enableDebugMode(true);
```

## 版本更新

### 模型版本迁移

当 Anthropic 发布新版本模型时：

1. 更新 `application.yml` 中的模型标识
2. 测试现有功能兼容性
3. 根据新模型特性调整提示词

### API 变更

关注 Anthropic API 官方文档，及时更新客户端代码。

## 相关服务

- **ModelManagerService**: 模型切换和管理
- **ChatService**: 统一对话接口
- **RagService**: 检索增强生成
- **MultimodalService**: 多模态处理
- **PersonalizedRecommendationService**: 个性化推荐

---

*文档版本: 1.0*  
*最后更新: 2024年*  
*适用项目: variance*
