package com.lowic.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowic.ai.entity.ChatSession;
import com.lowic.ai.service.SessionManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SessionControllerTest {

    @Mock
    private SessionManagerService sessionManagerService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new SessionController(sessionManagerService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateSession() throws Exception {
        ChatSession session = new ChatSession("session1", "user123", "Test Session", LocalDateTime.now());
        when(sessionManagerService.createSession(anyString(), anyString())).thenReturn(session);

        Map<String, String> request = new HashMap<>();
        request.put("userId", "user123");
        request.put("sessionName", "Test Session");

        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session1"))
                .andExpect(jsonPath("$.userId").value("user123"));
    }

    @Test
    void testListSessions() throws Exception {
        ChatSession session1 = new ChatSession("session1", "user123", "Session 1", LocalDateTime.now());
        ChatSession session2 = new ChatSession("session2", "user123", "Session 2", LocalDateTime.now());
        List<ChatSession> sessions = Arrays.asList(session1, session2);
        when(sessionManagerService.listSessions(anyString())).thenReturn(sessions);

        mockMvc.perform(get("/api/sessions/user/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].sessionId").value("session1"));
    }

    @Test
    void testGetSession() throws Exception {
        ChatSession session = new ChatSession("session1", "user123", "Test Session", LocalDateTime.now());
        when(sessionManagerService.getSession(anyString())).thenReturn(session);

        mockMvc.perform(get("/api/sessions/session1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session1"));
    }

    @Test
    void testGetSessionNotFound() throws Exception {
        when(sessionManagerService.getSession(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/sessions/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateSession() throws Exception {
        ChatSession session = new ChatSession("session1", "user123", "Updated Session", LocalDateTime.now());
        when(sessionManagerService.updateSession(anyString(), anyString())).thenReturn(session);

        Map<String, String> request = new HashMap<>();
        request.put("sessionName", "Updated Session");

        mockMvc.perform(put("/api/sessions/session1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionName").value("Updated Session"));
    }

    @Test
    void testDeleteSession() throws Exception {
        when(sessionManagerService.deleteSession(anyString())).thenReturn(true);

        mockMvc.perform(delete("/api/sessions/session1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testClearSessionMessages() throws Exception {
        when(sessionManagerService.clearSessionMessages(anyString())).thenReturn(true);

        mockMvc.perform(delete("/api/sessions/session1/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetSessionMessages() throws Exception {
        ChatSession session = new ChatSession("session1", "user123", "Test Session", LocalDateTime.now());
        when(sessionManagerService.getSession(anyString())).thenReturn(session);

        mockMvc.perform(get("/api/sessions/session1/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session1"));
    }
}
