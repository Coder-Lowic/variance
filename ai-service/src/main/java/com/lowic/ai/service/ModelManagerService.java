package com.lowic.ai.service;

import com.lowic.ai.model.ModelConfig;
import com.lowic.ai.model.ModelProvider;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaChatOptions;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.ai.vertexai.gemini.api.VertexAiGeminiApi;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ModelManagerService {
    private static final Logger log = LoggerFactory.getLogger(ModelManagerService.class);

    @Getter
    private volatile ModelConfig currentConfig;

    private final Map<ModelProvider, ChatClient> chatClientCache = new ConcurrentHashMap<>();

    public ModelManagerService() {
        this.currentConfig = ModelConfig.defaultOpenAI();
    }

    public void switchModel(ModelConfig config) {
        this.currentConfig = config;
        chatClientCache.remove(config.getProvider());
        log.info("Switched to model provider: {}, model: {}", config.getProvider().name(), config.getModelName());
    }

    public ChatClient getCurrentChatClient() {
        return getChatClient(currentConfig);
    }

    public ChatClient getChatClient(ModelConfig config) {
        return chatClientCache.computeIfAbsent(config.getProvider(), provider -> createChatClient(config));
    }

    private ChatClient createChatClient(ModelConfig config) {
        ChatClient.Builder builder = switch (config.getProvider()) {
            case OPENAI -> buildOpenAiChatClient(config);
            case ANTHROPIC -> buildAnthropicChatClient(config);
            case GEMINI -> buildGeminiChatClient(config);
            case OLLAMA -> buildOllamaChatClient(config);
        };
        log.info("Created ChatClient for provider: {}, model: {}", config.getProvider().name(), config.getModelName());
        return builder.build();
    }

    private ChatClient.Builder buildOpenAiChatClient(ModelConfig config) {
        String apiKey = resolveApiKey(config, "OPENAI_API_KEY");
        String baseUrl = config.getBaseUrl() != null ? config.getBaseUrl() : "https://api.openai.com";

        OpenAiApi api = new OpenAiApi(baseUrl, apiKey);
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(config.getModelName())
                .withTemperature(config.getTemperature())
                .withMaxTokens(config.getMaxTokens())
                .build();

        return ChatClient.builder(new OpenAiChatModel(api, options));
    }

    private ChatClient.Builder buildAnthropicChatClient(ModelConfig config) {
        String apiKey = resolveApiKey(config, "ANTHROPIC_API_KEY");

        AnthropicApi api = new AnthropicApi(apiKey);
        AnthropicChatOptions options = AnthropicChatOptions.builder()
                .withModel(config.getModelName())
                .withTemperature(config.getTemperature())
                .withMaxTokens(config.getMaxTokens())
                .build();

        return ChatClient.builder(new AnthropicChatModel(api, options));
    }

    private ChatClient.Builder buildGeminiChatClient(ModelConfig config) {
        String apiKey = resolveApiKey(config, "GEMINI_API_KEY");

        VertexAiGeminiApi api = new VertexAiGeminiApi(apiKey);
        VertexAiGeminiChatOptions options = VertexAiGeminiChatOptions.builder()
                .withModel(config.getModelName())
                .withTemperature(config.getTemperature())
                .withMaxTokens(config.getMaxTokens())
                .build();

        return ChatClient.builder(new VertexAiGeminiChatModel(api, options));
    }

    private ChatClient.Builder buildOllamaChatClient(ModelConfig config) {
        String baseUrl = config.getBaseUrl() != null ? config.getBaseUrl()
                : System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://localhost:11434");

        OllamaApi api = new OllamaApi(baseUrl);
        OllamaChatOptions options = OllamaChatOptions.builder()
                .withModel(config.getModelName())
                .withTemperature(config.getTemperature())
                .build();

        return ChatClient.builder(new OllamaChatModel(api, options));
    }

    /**
     * 解析API Key：优先使用config中的key，其次从环境变量读取
     */
    private String resolveApiKey(ModelConfig config, String envVarName) {
        if (config.getApiKey() != null && !config.getApiKey().isBlank()) {
            return config.getApiKey();
        }
        String envValue = System.getenv(envVarName);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        log.warn("API key not found for provider {}: neither config.apiKey nor env var {} is set",
                config.getProvider().name(), envVarName);
        return "";
    }

    public Map<ModelProvider, String> getAvailableModels() {
        Map<ModelProvider, String> models = new ConcurrentHashMap<>();
        models.put(ModelProvider.OPENAI, "gpt-4o, gpt-4, gpt-3.5-turbo");
        models.put(ModelProvider.ANTHROPIC, "claude-3-opus-20240229, claude-3-sonnet-20240229, claude-3-haiku-20240307");
        models.put(ModelProvider.GEMINI, "gemini-pro, gemini-ultra");
        models.put(ModelProvider.OLLAMA, "llama3, mistral, codellama");
        return models;
    }

    public ModelConfig getCurrentModelConfig() {
        return currentConfig;
    }
}
