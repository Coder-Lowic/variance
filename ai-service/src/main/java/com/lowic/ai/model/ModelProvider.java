package com.lowic.ai.model;

import lombok.Getter;

@Getter
public enum ModelProvider {
    OPENAI("openai", "OpenAI GPT系列"),
    ANTHROPIC("anthropic", "Anthropic Claude系列"),
    GEMINI("gemini", "Google Gemini系列"),
    OLLAMA("ollama", "Ollama本地模型");

    private final String code;
    private final String name;

    ModelProvider(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ModelProvider fromCode(String code) {
        for (ModelProvider provider : values()) {
            if (provider.code.equalsIgnoreCase(code)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown model provider: " + code);
    }
}
