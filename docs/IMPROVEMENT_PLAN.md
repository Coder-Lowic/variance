# Variance 项目修复与改进方案

> 基于 2026/07/04 全面代码审查生成

---

## 一、已完成的紧急修复（3 项）

### 1.1 ✅ 多模型切换真正实现

**问题**：`ModelManagerService.createChatClient()` 始终创建 `OpenAiChatModel`，Anthropic/Gemini/Ollama 的 Spring AI Starter 已声明但从未使用。

**修复**：`ai-service/.../service/ModelManagerService.java`

- 使用 Java 17 switch 表达式分派到 4 个独立工厂方法
- 每个 Provider 创建对应的 `*Api` → `*ChatOptions` → `*ChatModel`
- 新增 `resolveApiKey()` 统一处理 API Key 解析（config → 环境变量兜底）

### 1.2 ✅ 数据持久化

**问题**：`SessionManagerService` 和 `DocumentQAService` 全部使用 `ConcurrentHashMap` 内存存储，服务重启所有数据丢失。

**修复内容**：

| 层次 | 文件 | 变更 |
|------|------|------|
| 依赖 | `ai-service/pom.xml` | 新增 `spring-boot-starter-data-jpa` |
| 配置 | `ai-service/.../application.yml` | 新增 `spring.datasource` + `spring.jpa`（复用 PGVector 的 PostgreSQL） |
| 实体 | `ChatSession.java` | `@Entity` + `@OneToMany` messages |
| 实体 | `ChatMessage.java` | `@Entity` + `@Id` + `@ManyToOne` session |
| 新增 | `UserPreference.java` | 用户偏好持久化（JSON 列） |
| 新增 | `DocumentCache.java` | 上传文档内容缓存 |
| 新增 | `*Repository.java`（4 个） | Spring Data JPA 接口 |
| 重写 | `SessionManagerService.java` | ConcurrentHashMap → JPA + @Transactional |
| 重写 | `DocumentQAService.java` | ConcurrentHashMap → DocumentCacheRepository |

### 1.3 ✅ 空壳模块填充

**问题**：`variance-web` 和 `variance-service` 只有空的 `@SpringBootApplication` 主类。

**修复**：

| 模块 | 端口 | 新增内容 |
|------|------|----------|
| variance-web | 8080 | `IndexController`（`/` `/health` `/services`）、`application.yml`、Lombok + SpringDoc |
| variance-service | 8082 | `OrchestrationController`（`/health` `/`）、`application.yml`（下游服务配置）、Lombok + SpringDoc |

---

## 二、待修复问题（按优先级排序）

### 🔴 高优先级（影响生产可用性）

#### 2.1 添加用户认证与鉴权

**现状**：所有 API 端点完全无保护，任何人可直接调用。

**方案**：
```
ai-service/pom.xml
  └── 新增 spring-boot-starter-security
  └── 新增 jjwt (io.jsonwebtoken)

新增文件：
  ai-service/.../config/SecurityConfig.java          — Spring Security 配置
  ai-service/.../config/JwtAuthFilter.java           — JWT 认证过滤器
  ai-service/.../controller/AuthController.java      — /api/auth/login, /api/auth/register
  ai-service/.../service/AuthService.java            — 用户认证逻辑
  ai-service/.../entity/User.java                    — 用户实体
  ai-service/.../repository/UserRepository.java      — 用户仓库
```

**预估工作量**：1-2 天

---

#### 2.2 流式输出（SSE）

**现状**：AI 对话返回完整响应后才一次返回，用户体验差。

**方案**：
```java
// ChatController.java — 新增 SSE 端点
@PostMapping(value = "/send/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<String>> sendMessageStream(@RequestBody Map<String, String> request) {
    return chatClient.prompt()
            .user(request.get("message"))
            .stream()
            .content()
            .map(content -> ServerSentEvent.<String>builder()
                    .data(content)
                    .build());
}
```

Spring AI `ChatClient` 已内置 `stream()` 支持，改动量小。

**预估工作量**：0.5 天

---

#### 2.3 API 请求 DTO 化

**现状**：大量 Controller 使用 `Map<String, Object>` / `Map<String, String>` 作为请求体，无编译时类型安全，无校验。

**方案**：
```
新增 ai-service/.../dto/ 包：
  ChatRequest.java          — message, sessionId, k
  ModelSwitchRequest.java   — provider, model
  ModerationRequest.java    — content, contentId, checkSensitiveInfo, checkAI
  DocumentUploadRequest.java — file, metadata
  ...

所有 Controller 的 @RequestBody Map 替换为具体 DTO
添加 @Valid + Bean Validation 注解（@NotBlank, @Size, @Min...）
GlobalExceptionHandler 新增 MethodArgumentNotValidException 处理器
```

**预估工作量**：1 天

---

#### 2.4 数据库迁移脚本（Flyway）

**现状**：零 `.sql` 文件，JPA `ddl-auto: update` 仅适合开发环境。

**方案**：
```
ai-service/pom.xml → 新增 flyway-core + flyway-postgresql
application.yml → spring.jpa.hibernate.ddl-auto: validate（替代 update）
新增资源目录 → ai-service/src/main/resources/db/migration/

V001__create_chat_tables.sql:
  CREATE TABLE chat_session (...)
  CREATE TABLE chat_message (...)
  CREATE TABLE user_preference (...)
  CREATE TABLE document_cache (...)
```

**预估工作量**：0.5 天

---

### 🟡 中优先级（提升工程质量）

#### 2.5 统一依赖注入风格

**现状**：ai-service 用构造器注入，data-analysis 用 `@Resource` 字段注入，`ContentModerationService` 用 `@Autowired`。

**方案**：
```
全项目统一为：构造器注入 + Lombok @RequiredArgsConstructor

示例：
  @Service
  @RequiredArgsConstructor
  public class AnalysisServiceImpl implements IAnalysisService {
      private final SpAdRpMapper spAdRpMapper;
      private final SdAdRpMapper sdAdRpMapper;
      // Lombok 自动生成构造器
  }
```

涉及文件：data-analysis 模块 ~10 个 Service + Controller

**预估工作量**：0.5 天

---

#### 2.6 减少 ChatService 职责

**现状**：`ChatService`（448 行）注入 7 个依赖，大量方法是纯委托（delegation）。

**方案**：
```
现状：ChatController → ChatService → DocumentGeneratorService
改为：ChatController → DocumentGeneratorService（直接注入）

Controller 应直接注入所需的具体 Service，ChatService 仅保留对话核心逻辑：
  - chat, chatWithSystemPrompt
  - chatWithSession, chatWithSessionAndSystemPrompt
  - chatWithImageFile, chatWithVoice
  - switchModel, getAvailableModels, getCurrentModel

移除 ChatService 中的委托方法（~200 行），Controller 直接调用对应 Service
```

**预估工作量**：0.5 天

---

#### 2.7 消除重复错误处理

**现状**：`GlobalExceptionHandler` 已全局拦截，但 `ContentModerationController`、`DocumentQAController` 中仍有 try/catch 返回 400。

**方案**：
```java
// 新增自定义异常
public class DocumentNotFoundException extends RuntimeException { ... }
public class ModerationFailedException extends RuntimeException { ... }
public class SessionNotFoundException extends RuntimeException { ... }

// GlobalExceptionHandler 新增处理器
@ExceptionHandler(DocumentNotFoundException.class)
public ResponseEntity<?> handleDocumentNotFound(DocumentNotFoundException e) {
    return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
}

// Controller 中删除所有 try/catch，改为 throw new XxxException("...")
```

**预估工作量**：0.5 天

---

#### 2.8 ContentModerationService 正则预编译

**现状**：每次 `filterSensitiveInfo()` 调用都重新 `Pattern.compile()`。

**方案**：
```java
private static final Pattern PHONE_PATTERN = Pattern.compile("1[3-9]\\d{9}");
private static final Pattern EMAIL_PATTERN = Pattern.compile("\\w+@\\w+\\.\\w+");
private static final Pattern ID_CARD_PATTERN = Pattern.compile("\\d{17}[\\dXx]");
private static final Pattern BANK_CARD_PATTERN = Pattern.compile("\\d{16,19}");
```

**预估工作量**：5 分钟

---

#### 2.9 补全 data-analysis 空 Controller

**现状**：5 个 Controller（`BusinessReportController`、`ProductionInfoController`、`SbCampRpController`、`SbInfoController`、`SpAdRpController`）为空类。

**方案**：
```
实现标准 CRUD REST API，使用 MyBatis-Plus 的 ServiceImpl：

@RestController
@RequestMapping("/api/business-reports")
@RequiredArgsConstructor
public class BusinessReportController {
    private final IBusinessReportService service;

    @GetMapping
    public ResponseEntity<Page<BusinessReport>> list(PageQuery query) { ... }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessReport> get(@PathVariable Long id) { ... }

    @PostMapping
    public ResponseEntity<BusinessReport> create(@RequestBody BusinessReport report) { ... }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessReport> update(@PathVariable Long id, @RequestBody BusinessReport report) { ... }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { ... }
}
```

**预估工作量**：1 天

---

#### 2.10 API 版本化

**现状**：所有 API 无版本前缀（`/api/chat/send`），未来变更无向后兼容保障。

**方案**：
```java
// 添加版本前缀（可与原路径共存，渐进迁移）
@RestController
@RequestMapping("/api/v1/chat")
public class ChatControllerV1 { ... }

// 或在 application.yml 配置 context-path
server.servlet.context-path: /api/v1
```

**预估工作量**：0.5 天

---

#### 2.11 data-analysis 测试恢复

**现状**：`maven.test.skip=true`，整个 data-analysis 模块无测试。

**方案**：
```
1. 移除 pom.xml 中的 maven.test.skip=true
2. 修复 Spring AI 1.0.0-M5 配置导致的 contextLoads 失败
3. 为重点 Service 添加单元测试：
   - AIAnalysisService 测试
   - AnalysisServiceImpl 测试（复杂 SQL 分析）
   - IExportServiceImpl 测试
4. 使用 @MybatisPlusTest + H2 内存数据库做 Mapper 集成测试
```

**预估工作量**：2-3 天

---

### 🟢 低优先级（功能增强）

#### 2.12 对接 Amazon SP-API

自动拉取广告数据替代 Excel 手动上传。

#### 2.13 数据可视化 API

对接 ECharts，为前端提供聚合图表数据接口。

#### 2.14 定时任务

`@Scheduled` 自动拉取数据、自动生成周报/月报。

#### 2.15 告警通知

ACOS 超标、销量异常告警，支持邮件 / 钉钉 / 企微。

#### 2.16 国际化

全中文硬编码 → `messages.properties` + `MessageSource`。

#### 2.17 Function Calling

让 AI 调用业务 API（查数据库、导出报表、切换模型）。

#### 2.18 MCP Server

将 Variance 暴露为 MCP Server，让 Claude Desktop 等客户端直接调用。

#### 2.19 前端界面

基于 React / Vue 构建管理后台。

---

## 三、修复执行建议

### 第一阶段（本周）— 达到可演示水平

| 序号 | 事项 | 预估 |
|------|------|------|
| 2.8 | 正则预编译 | 5 min |
| 2.7 | 消除重复错误处理 | 0.5 day |
| 2.5 | 统一 DI 风格 | 0.5 day |
| 2.3 | API 请求 DTO 化 | 1 day |
| 2.6 | 减少 ChatService 职责 | 0.5 day |

**小计**：~2.5 天

### 第二阶段（下周）— 达到生产可用水平

| 序号 | 事项 | 预估 |
|------|------|------|
| 2.1 | 用户认证与鉴权 | 1-2 day |
| 2.2 | 流式输出（SSE） | 0.5 day |
| 2.4 | Flyway 数据库迁移 | 0.5 day |
| 2.10 | API 版本化 | 0.5 day |
| 2.9 | 补全 data-analysis 空 Controller | 1 day |

**小计**：~4 天

### 第三阶段（后续）— 完善与增强

| 序号 | 事项 | 预估 |
|------|------|------|
| 2.11 | data-analysis 测试恢复 | 2-3 day |
| 2.12-2.19 | 功能增强项 | 按需排期 |

---

## 四、已修改文件索引

```
修复 #1 — 多模型切换:
  ai-service/src/main/java/com/lowic/ai/service/ModelManagerService.java  [MODIFIED]

修复 #2 — 数据持久化:
  ai-service/pom.xml                                                       [MODIFIED]
  ai-service/src/main/resources/application.yml                            [MODIFIED]
  ai-service/src/main/java/com/lowic/ai/entity/ChatSession.java           [MODIFIED]
  ai-service/src/main/java/com/lowic/ai/entity/ChatMessage.java           [MODIFIED]
  ai-service/src/main/java/com/lowic/ai/entity/UserPreference.java        [NEW]
  ai-service/src/main/java/com/lowic/ai/entity/DocumentCache.java         [NEW]
  ai-service/src/main/java/com/lowic/ai/repository/ChatSessionRepository.java  [NEW]
  ai-service/src/main/java/com/lowic/ai/repository/ChatMessageRepository.java  [NEW]
  ai-service/src/main/java/com/lowic/ai/repository/UserPreferenceRepository.java [NEW]
  ai-service/src/main/java/com/lowic/ai/repository/DocumentCacheRepository.java  [NEW]
  ai-service/src/main/java/com/lowic/ai/service/SessionManagerService.java [MODIFIED]
  ai-service/src/main/java/com/lowic/ai/service/DocumentQAService.java    [MODIFIED]

修复 #3 — 空壳模块:
  variance-web/pom.xml                                                     [MODIFIED]
  variance-web/src/main/resources/application.yml                          [NEW]
  variance-web/src/main/java/.../controller/IndexController.java           [NEW]
  variance-service/pom.xml                                                 [MODIFIED]
  variance-service/src/main/resources/application.yml                      [NEW]
  variance-service/src/main/java/.../controller/OrchestrationController.java [NEW]
```

---

*文档版本：1.0 | 生成日期：2026/07/04*
