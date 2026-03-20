package com.lowic.ai.model;

import lombok.Data;

@Data
public class ModelConfig {
    private ModelProvider provider;
    private String modelName;
    private Double temperature;
    private Integer maxTokens;
    private Double topP;
    private String apiKey;
    private String baseUrl;

    public static ModelConfig defaultOpenAI() {
        ModelConfig config = new ModelConfig();
        config.setProvider(ModelProvider.OPENAI);
        config.setModelName("gpt-4");
        config.setTemperature(0.7);
        config.setMaxTokens(2048);
        config.setTopP(1.0);
        return config;
    }

    public static ModelConfig defaultAnthropic() {
        ModelConfig config = new ModelConfig();
        config.setProvider(ModelProvider.ANTHROPIC);
        config.setModelName("claude-3-opus-20240229");
        config.setTemperature(0.7);
        config.setMaxTokens(2048);
        config.setTopP(1.0);
        return config;
    }

    public static ModelConfig defaultGemini() {
        ModelConfig config = new ModelConfig();
        config.setProvider(ModelProvider.GEMINI);
        config.setModelName("gemini-pro");
        config.setTemperature(0.7);
        config.setMaxTokens(2048);
        config.setTopP(1.0);
        return config;
    }

    public static ModelConfig defaultOllama() {
        ModelConfig config = new ModelConfig();
        config.setProvider(ModelProvider.OLLAMA);
        config.setModelName("llama3");
        config.setTemperature(0.7);
        config.setMaxTokens(2048);
        config.setTopP(1.0);
        config.setBaseUrl("http://localhost:11434");
        return config;
    }
}
