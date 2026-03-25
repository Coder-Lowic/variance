package com.lowic.ai.model;

public enum SpeechModel {
    WHISPER("whisper", "OpenAI Whisper 语音识别模型"),
    OLLAMA("ollama", "Ollama 本地语音识别模型");

    private final String name;
    private final String description;

    SpeechModel(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static SpeechModel fromName(String name) {
        for (SpeechModel model : values()) {
            if (model.name.equalsIgnoreCase(name)) {
                return model;
            }
        }
        return WHISPER; // 默认使用 Whisper
    }
}
