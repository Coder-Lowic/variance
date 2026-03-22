# 🚀 Variance - 基于 Spring AI 的智能数据分析平台

## 🌟 项目简介

Variance 是一个现代化的智能数据分析平台，深度集成 **Spring AI** 框架，致力于通过**人工智能**和**大语言模型**技术，为企业提供一站式的数据分析与可视化解决方案。我们相信数据驱动的决策将重塑未来商业格局。

## ✨ 核心特性

### 🤖 Spring AI 原生集成
- **多模型支持**：原生支持 OpenAI GPT-4、Anthropic Claude、Google Gemini、Ollama 等主流大模型
- **灵活切换**：运行时动态切换模型提供商，无需重启服务
- **向量数据库**：集成 PGVector、Milvus 等向量存储，支持 RAG 检索增强生成
- **Chat Client**：Spring AI 高级 ChatClient，支持流式输出、函数调用
- **附件对话**：支持上传 PDF、Word、Excel、TXT、Markdown 等多种格式附件进行对话
- **多模态支持**：文本、图像、PDF 等多格式文档解析与处理

### 📊 智能数据处理
- **自动化数据导入**：支持Excel、CSV等多格式数据的智能解析与入库
- **AI增强清洗**：利用大模型进行数据质量检测与自动清洗
- **灵活规则引擎**：可自定义数据处理规则，满足多样化业务需求

### � AI 驱动分析
- **智能报表生成**：基于大语言模型自动生成数据分析报告
- **趋势预测**：结合机器学习算法进行业务趋势预测
- **异常检测**：AI驱动的异常数据自动识别与告警
- **广告效果分析**：智能评估广告投放ROI与优化建议

### 📈 可视化展示
- **动态仪表盘**：实时数据可视化，多维度业务监控
- **智能图表推荐**：根据数据特征自动推荐最佳可视化方式
- **交互式分析**：支持下钻、联动、筛选等丰富的交互功能

### 🔄 全流程自动化
- **爬虫数据集成**：支持多源数据的自动采集与整合
- **定时任务调度**：灵活的任务调度与自动化工作流
- **导出与分享**：一键导出精美报表，支持多格式输出

## 🛠️ 技术架构

### 核心技术栈
- **后端框架**：Spring Boot 3.2.5 + Spring AI 1.0.0-M5 + JDK 17
- **持久层**：MyBatis-Plus 3.5.5
- **AI框架**：Spring AI (OpenAI、Anthropic、Google Gemini、Ollama、PGVector)
- **文档API**：SpringDoc OpenAPI 3.0
- **工具生态**：Hutool 5.8.26、Apache POI 5.2.5
- **对象映射**：MapStruct 1.1.0

### 系统架构
```
┌─────────────────────────────────────────────────────────────────────┐
│                           前端展示层                                  │
│  (Web UI / 数据大屏 / 移动端 / AI 智能助手)                           │
└─────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────┐
│                         API 网关层                                    │
│  (路由转发 / 权限控制 / 流量控制 / API 管理)                           │
└─────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────┐
│                       业务服务层                                      │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐  │
│  │  数据分析服务    │  │  AI 智能服务      │  │  报表引擎服务    │  │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘  │
│  ┌──────────────────┐  ┌──────────────────┐                          │
│  │  RAG 检索服务     │  │  向量存储服务    │                          │
│  └──────────────────┘  └──────────────────┘                          │
└─────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────┐
│                       数据存储层                                      │
│  (MySQL / Redis / PGVector / 向量数据库 / 文件存储 / 对象存储)        │
└─────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────┐
│                       AI 模型层                                       │
│  (OpenAI GPT-4 / Claude 3 / Llama 3 / 自定义模型)                  │
└─────────────────────────────────────────────────────────────────────┘
```

## 📦 项目模块

```
variance/
├── variance-web              # Web 入口模块
├── variance-service          # 核心服务模块
├── data-analysis             # 数据分析模块
│   ├── 业务报表分析
│   ├── 商品信息管理
│   ├── 广告投放分析
│   ├── 数据导出服务
│   └── AI 分析服务 (Spring AI 集成)
└── ai-service                # AI 智能服务模块
    ├── Chat 对话服务
    ├── RAG 检索增强生成
    ├── 向量存储管理
    ├── 数据分析 AI 服务
    └── 多模态文档处理
```

## 🎯 发展路线图

### Phase 1: 基础数据平台 ✅
- [x] 自定义Excel导入与导出
- [x] 数据落库与规则化展示
- [x] 基础报表生成能力

### Phase 2: Spring AI 架构升级 ✅
- [x] 升级至 Spring Boot 3.2.5 + JDK 17
- [x] 集成 Spring AI 1.0.0-M5
- [x] 创建独立的 AI 服务模块
- [x] 支持 OpenAI GPT-4 大模型
- [x] 集成 PGVector 向量数据库
- [x] 实现 RAG 检索增强生成
- [x] Swagger → SpringDoc OpenAPI 3.0
- [x] 多模型支持 (OpenAI、Anthropic、Gemini、Ollama)
- [x] 运行时模型动态切换
- [x] 附件对话功能 (PDF、Word、Excel、TXT、Markdown)

### Phase 3: 智能分析引擎 🚧
- [ ] AI智能报表生成
- [ ] 数据趋势预测
- [ ] 异常检测与告警
- [ ] 多模态数据支持
- [ ] 函数调用与工具集成

### Phase 4: 企业级平台 🔮
- [ ] 多租户SaaS架构
- [ ] 自定义分析模板市场
- [ ] AI Assistant 智能助手
- [ ] 生态系统开放API
- [ ] 私有化部署方案

## 🚀 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- PostgreSQL 15+ (用于 PGVector)

### 配置说明

#### AI 服务配置 (application.yml)
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY:}
      chat:
        options:
          model: gpt-4
          temperature: 0.7
      embedding:
        options:
          model: text-embedding-ada-002
    anthropic:
      api-key: ${ANTHROPIC_API_KEY:}
      chat:
        options:
          model: claude-3-opus-20240229
    vertex-ai:
      gemini:
        api-key: ${GEMINI_API_KEY:}
        chat:
          options:
            model: gemini-pro
    ollama:
      base-url: ${OLLAMA_BASE_URL:http://localhost:11434}
      chat:
        options:
          model: llama3
    vectorstore:
      pgvector:
        jdbc-url: jdbc:postgresql://localhost:5432/vector_db
        username: postgres
        password: postgres
```

#### 模型切换 API
```bash
# 查看当前模型
GET /api/ai/model/current

# 查看可用模型列表
GET /api/ai/model/available

# 切换到 OpenAI GPT-4
POST /api/ai/model/switch/openai?modelName=gpt-4

# 切换到 Anthropic Claude
POST /api/ai/model/switch/anthropic?modelName=claude-3-opus-20240229

# 切换到 Google Gemini
POST /api/ai/model/switch/gemini?modelName=gemini-pro

# 切换到 Ollama 本地模型
POST /api/ai/model/switch/ollama?modelName=llama3&baseUrl=http://localhost:11434

# 自定义模型配置
POST /api/ai/model/switch/custom
Content-Type: application/json
{
  "provider": "openai",
  "modelName": "gpt-4",
  "temperature": 0.7,
  "maxTokens": 2048,
  "apiKey": "your-custom-api-key",
  "baseUrl": "https://your-custom-endpoint.com"
}
```

### 核心 API 端点

#### AI 对话服务
- `POST /api/ai/chat` - 基础对话
- `POST /api/ai/chat/system` - 带系统提示的对话
- `POST /api/ai/chat/attachment` - 带附件的对话
- `POST /api/ai/chat/attachment/system` - 带附件和系统提示的对话
- `GET /api/ai/chat/supported-formats` - 获取支持的文件格式

#### 附件对话 API 使用示例
```bash
# 查看支持的文件格式
GET /api/ai/chat/supported-formats

# 上传附件并对话 (PDF/Word/Excel/TXT/MD)
POST /api/ai/chat/attachment
Content-Type: multipart/form-data
Form-Data:
  message: "请总结这个文档的主要内容"
  file: @document.pdf

# 带系统提示的附件对话
POST /api/ai/chat/attachment/system
Content-Type: multipart/form-data
Form-Data:
  systemPrompt: "你是一位专业的数据分析师"
  message: "请分析这个Excel表格中的数据"
  file: @data.xlsx
```

#### RAG 检索服务
- `POST /api/ai/rag/documents` - 添加文档到向量库
- `POST /api/ai/rag/query` - RAG 智能问答

#### 数据分析 AI 服务
- `POST /api/analysis/ai/report` - 生成 AI 分析报告
- `POST /api/analysis/ai/ad-performance` - 广告效果分析
- `POST /api/analysis/ai/forecast` - 销售预测

#### API 文档
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## 🤝 贡献指南

我们欢迎任何形式的贡献！无论是提交Issue、改进文档还是贡献代码。

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

<p align="center">
  <b>用数据驱动决策，让AI赋能未来 | Powered by Spring AI</b>
</p>
