package com.lowic.ai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_preference")
public class UserPreference {

    @Id
    @Column(length = 64)
    private String userId;

    @Column(columnDefinition = "TEXT")
    private String preferencesJson;

    public UserPreference() {
    }

    public UserPreference(String userId, String preferencesJson) {
        this.userId = userId;
        this.preferencesJson = preferencesJson;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPreferencesJson() {
        return preferencesJson;
    }

    public void setPreferencesJson(String preferencesJson) {
        this.preferencesJson = preferencesJson;
    }
}
