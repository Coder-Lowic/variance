package com.lowic.ai.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ContentModerationResult {
    private String contentId;
    private String originalContent;
    private boolean isSafe;
    private String overallRiskLevel;
    private List<ModerationCategory> categories;
    private Map<String, Object> details;
    private LocalDateTime moderationTime;
    private String moderatedBy;

    public ContentModerationResult() {
    }

    public ContentModerationResult(String contentId, String originalContent, boolean isSafe, 
                                    String overallRiskLevel, List<ModerationCategory> categories, 
                                    Map<String, Object> details, LocalDateTime moderationTime, String moderatedBy) {
        this.contentId = contentId;
        this.originalContent = originalContent;
        this.isSafe = isSafe;
        this.overallRiskLevel = overallRiskLevel;
        this.categories = categories;
        this.details = details;
        this.moderationTime = moderationTime;
        this.moderatedBy = moderatedBy;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public boolean isSafe() {
        return isSafe;
    }

    public void setSafe(boolean safe) {
        isSafe = safe;
    }

    public String getOverallRiskLevel() {
        return overallRiskLevel;
    }

    public void setOverallRiskLevel(String overallRiskLevel) {
        this.overallRiskLevel = overallRiskLevel;
    }

    public List<ModerationCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<ModerationCategory> categories) {
        this.categories = categories;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public LocalDateTime getModerationTime() {
        return moderationTime;
    }

    public void setModerationTime(LocalDateTime moderationTime) {
        this.moderationTime = moderationTime;
    }

    public String getModeratedBy() {
        return moderatedBy;
    }

    public void setModeratedBy(String moderatedBy) {
        this.moderatedBy = moderatedBy;
    }
}
