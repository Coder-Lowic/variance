package com.lowic.ai.entity;

import java.util.Map;

public class DocumentQARequest {
    private String question;
    private String documentContent;
    private String documentName;
    private Map<String, Object> metadata;
    private int k = 3;
    private boolean useHistory = false;
    private String sessionId;

    public DocumentQARequest() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public boolean isUseHistory() {
        return useHistory;
    }

    public void setUseHistory(boolean useHistory) {
        this.useHistory = useHistory;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
