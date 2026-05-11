package com.lowic.ai.entity;

public enum ModerationRiskLevel {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
    VERY_HIGH("very_high");

    private final String value;

    ModerationRiskLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
