package com.lowic.ai.entity;

public class ModerationCategory {
    private String categoryName;
    private boolean flagged;
    private double score;
    private String severityLevel;

    public ModerationCategory() {
    }

    public ModerationCategory(String categoryName, boolean flagged, double score, String severityLevel) {
        this.categoryName = categoryName;
        this.flagged = flagged;
        this.score = score;
        this.severityLevel = severityLevel;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(String severityLevel) {
        this.severityLevel = severityLevel;
    }
}
