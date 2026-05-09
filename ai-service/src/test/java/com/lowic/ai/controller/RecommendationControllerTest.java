package com.lowic.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowic.ai.service.PersonalizedRecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.document.Document;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RecommendationControllerTest {

    @Mock
    private PersonalizedRecommendationService recommendationService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new RecommendationController(recommendationService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testAnalyzeUserPreferences() throws Exception {
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("interests", Arrays.asList("AI", "Technology"));
        preferences.put("language", "Chinese");
        when(recommendationService.analyzeUserPreferences(anyString())).thenReturn(preferences);

        mockMvc.perform(get("/api/recommendations/users/user123/preferences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interests.length()").value(2))
                .andExpect(jsonPath("$.language").value("Chinese"));
    }

    @Test
    void testGetRecommendations() throws Exception {
        List<String> recommendations = Arrays.asList("Recommendation 1", "Recommendation 2");
        when(recommendationService.generateRecommendations(anyString(), anyString(), anyInt())).thenReturn(recommendations);

        mockMvc.perform(get("/api/recommendations/users/user123")
                        .param("context", "test context")
                        .param("count", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value("Recommendation 1"));
    }

    @Test
    void testGetRAGBasedRecommendations() throws Exception {
        List<String> recommendations = Arrays.asList("RAG Rec 1", "RAG Rec 2");
        when(recommendationService.generateRAGBasedRecommendations(anyString(), anyString(), anyInt())).thenReturn(recommendations);

        mockMvc.perform(get("/api/recommendations/users/user123/rag")
                        .param("context", "test")
                        .param("count", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGenerateWelcomeMessage() throws Exception {
        when(recommendationService.generatePersonalizedWelcomeMessage(anyString())).thenReturn("Welcome back!");

        mockMvc.perform(get("/api/recommendations/users/user123/welcome"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome back!"));
    }

    @Test
    void testGenerateContentSuggestions() throws Exception {
        List<String> suggestions = Arrays.asList("Suggestion 1", "Suggestion 2");
        when(recommendationService.generateContentSuggestions(anyString(), anyString(), anyInt())).thenReturn(suggestions);

        mockMvc.perform(get("/api/recommendations/users/user123/content")
                        .param("contentType", "article")
                        .param("count", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testRecommendDocuments() throws Exception {
        List<Document> documents = Arrays.asList(
                new Document("doc1", "Content 1"),
                new Document("doc2", "Content 2")
        );
        when(recommendationService.recommendDocuments(anyString(), anyString(), anyInt())).thenReturn(documents);

        mockMvc.perform(get("/api/recommendations/users/user123/documents")
                        .param("query", "test")
                        .param("count", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGeneratePersonalizedAnswer() throws Exception {
        when(recommendationService.generatePersonalizedAnswer(anyString(), anyString())).thenReturn("Personalized answer");

        Map<String, String> request = new HashMap<>();
        request.put("question", "What is AI?");

        mockMvc.perform(post("/api/recommendations/users/user123/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Personalized answer"));
    }

    @Test
    void testGeneratePersonalizedContent() throws Exception {
        when(recommendationService.generatePersonalizedContent(anyString(), anyString())).thenReturn("Personalized content");

        Map<String, String> request = new HashMap<>();
        request.put("template", "email");

        mockMvc.perform(post("/api/recommendations/users/user123/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Personalized content"));
    }
}
