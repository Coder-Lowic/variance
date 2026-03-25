package com.lowic.ai.service;

import com.lowic.ai.model.ModelConfig;
import com.lowic.ai.model.ModelProvider;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
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
        // 简化实现，所有提供商都使用OpenAI模型作为默认实现
        return createOpenAIChatClient(config).build();
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

    public Map<ModelProvider, String> getAvailableModels() {
        Map<ModelProvider, String> models = new ConcurrentHashMap<>();
        models.put(ModelProvider.OPENAI, "gpt-4, gpt-3.5-turbo");
        models.put(ModelProvider.ANTHROPIC, "claude-3-opus-20240229, claude-3-sonnet-20240229, claude-3-haiku-20240307");
        models.put(ModelProvider.GEMINI, "gemini-pro, gemini-ultra");
        models.put(ModelProvider.OLLAMA, "llama3, mistral, codellama");
        return models;
    }
}
