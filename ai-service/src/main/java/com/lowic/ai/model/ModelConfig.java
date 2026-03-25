package com.lowic.ai.model;

public class ModelConfig {
    private ModelProvider provider;
    private String modelName;
    private Double temperature;
    private Integer maxTokens;
    private Double topP;
    private String apiKey;
    private String baseUrl;

    public ModelProvider getProvider() {
        return provider;
    }

    public void setProvider(ModelProvider provider) {
        this.provider = provider;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

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
