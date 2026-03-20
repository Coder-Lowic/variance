package com.lowic.ai.service;

import com.lowic.ai.model.ModelConfig;
import com.lowic.ai.model.ModelProvider;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaChatOptions;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ModelManagerService {

    @Getter
    private volatile ModelConfig currentConfig;

    private final Map<ModelProvider, ChatClient> chatClientCache = new ConcurrentHashMap<>();

    public ModelManagerService() {
        this.currentConfig = ModelConfig.defaultOpenAI();
    }

    public void switchModel(ModelConfig config) {
        this.currentConfig = config;
        chatClientCache.remove(config.getProvider());
        log.info("Switched to model provider: {}, model: {}", config.getProvider().getName(), config.getModelName());
    }

    public ChatClient getCurrentChatClient() {
        return getChatClient(currentConfig);
    }

    public ChatClient getChatClient(ModelConfig config) {
        return chatClientCache.computeIfAbsent(config.getProvider(), provider -> createChatClient(config));
    }

    private ChatClient createChatClient(ModelConfig config) {
        ChatClient.Builder builder;
        
        switch (config.getProvider()) {
            case OPENAI:
                builder = createOpenAIChatClient(config);
                break;
            case ANTHROPIC:
                builder = createAnthropicChatClient(config);
                break;
            case GEMINI:
                builder = createGeminiChatClient(config);
                break;
            case OLLAMA:
                builder = createOllamaChatClient(config);
                break;
            default:
                throw new IllegalArgumentException("Unsupported model provider: " + config.getProvider());
        }
        
        return builder.build();
    }

    private ChatClient.Builder createOpenAIChatClient(ModelConfig config) {
        String apiKey = config.getApiKey() != null ? config.getApiKey() : System.getenv("OPENAI_API_KEY");
        String baseUrl = config.getBaseUrl() != null ? config.getBaseUrl() : "https://api.openai.com";
        
        OpenAiApi openAiApi = new OpenAiApi(baseUrl, apiKey);
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(config.getModelName())
                .withTemperature(config.getTemperature())
                .withMaxTokens(config.getMaxTokens())
                .build();
        
        OpenAiChatModel chatModel = new OpenAiChatModel(openAiApi, options);
        return ChatClient.builder(chatModel);
    }

    private ChatClient.Builder createAnthropicChatClient(ModelConfig config) {
        String apiKey = config.getApiKey() != null ? config.getApiKey() : System.getenv("ANTHROPIC_API_KEY");
        
        AnthropicApi anthropicApi = new AnthropicApi(apiKey);
        AnthropicChatOptions options = AnthropicChatOptions.builder()
                .withModel(config.getModelName())
                .withTemperature(config.getTemperature())
                .withMaxTokens(config.getMaxTokens())
                .build();
        
        AnthropicChatModel chatModel = new AnthropicChatModel(anthropicApi, options);
        return ChatClient.builder(chatModel);
    }

    private ChatClient.Builder createGeminiChatClient(ModelConfig config) {
        String apiKey = config.getApiKey() != null ? config.getApiKey() : System.getenv("GEMINI_API_KEY");
        
        VertexAiGeminiChatOptions options = VertexAiGeminiChatOptions.builder()
                .withModel(config.getModelName())
                .withTemperature(config.getTemperature())
                .build();
        
        VertexAiGeminiChatModel chatModel = new VertexAiGeminiChatModel(apiKey, options);
        return ChatClient.builder(chatModel);
    }

    private ChatClient.Builder createOllamaChatClient(ModelConfig config) {
        String baseUrl = config.getBaseUrl() != null ? config.getBaseUrl() : "http://localhost:11434";
        
        OllamaApi ollamaApi = new OllamaApi(baseUrl);
        OllamaChatOptions options = OllamaChatOptions.builder()
                .withModel(config.getModelName())
                .withTemperature(config.getTemperature())
                .build();
        
        OllamaChatModel chatModel = new OllamaChatModel(ollamaApi, options);
        return ChatClient.builder(chatModel);
    }

    public Map<ModelProvider, String> getAvailableModels() {
        Map<ModelProvider, String> models = new ConcurrentHashMap<>();
        models.put(ModelProvider.OPENAI, "gpt-4, gpt-3.5-turbo");
        models.put(ModelProvider.ANTHROPIC, "claude-3-opus-20240229, claude-3-sonnet-20240229, claude-3-haiku-20240307");
        models.put(ModelProvider.GEMINI, "gemini-pro, gemini-ultra");
        models.put(ModelProvider.OLLAMA, "llama3, mistral, codellama");
        return models;
    }
}
