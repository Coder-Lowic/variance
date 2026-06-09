package com.lowic.ai.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DocumentQAResult {
    private String question;
    private String answer;
    private String documentName;
    private List<String> relevantSnippets;
    private double confidenceScore;
    private Map<String, Object> metadata;
    private LocalDateTime timestamp;
    private String sessionId;

    public DocumentQAResult() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public List<String> getRelevantSnippets() {
        return relevantSnippets;
    }

    public void setRelevantSnippets(List<String> relevantSnippets) {
        this.relevantSnippets = relevantSnippets;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
