package com.lowic.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowic.ai.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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

class ChatControllerTest {

    @Mock
    private ChatService chatService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new ChatController(chatService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testSendMessage() throws Exception {
        when(chatService.chat(anyString())).thenReturn("Hello! How can I help you?");

        Map<String, String> request = new HashMap<>();
        request.put("message", "Hello");

        mockMvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello! How can I help you?"));
    }

    @Test
    void testSendMessageWithSystemPrompt() throws Exception {
        when(chatService.chatWithSystemPrompt(anyString(), anyString())).thenReturn("System response");

        Map<String, String> request = new HashMap<>();
        request.put("systemPrompt", "You are a helpful assistant");
        request.put("message", "Hello");

        mockMvc.perform(post("/api/chat/send/system")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("System response"));
    }

    @Test
    void testChatWithRAG() throws Exception {
        when(chatService.chatWithRAG(anyString(), anyInt())).thenReturn("RAG enhanced response");

        Map<String, Object> request = new HashMap<>();
        request.put("message", "What is AI?");
        request.put("k", 3);

        mockMvc.perform(post("/api/chat/rag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("RAG enhanced response"));
    }

    @Test
    void testChatWithImage() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());
        when(chatService.chatWithRAGAndImage(any(), anyString(), anyInt())).thenReturn("Image analysis result");

        mockMvc.perform(multipart("/api/chat/image")
                        .file(imageFile)
                        .param("prompt", "Describe this image")
                        .param("k", "3"))
                .andExpect(status().isOk())
                .andExpect(content().string("Image analysis result"));
    }

    @Test
    void testSwitchModel() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("provider", "openai");
        request.put("model", "gpt-4");

        mockMvc.perform(post("/api/chat/model")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetAvailableModels() throws Exception {
        List<Map<String, String>> models = Arrays.asList(
                Map.of("provider", "openai", "model", "gpt-4"),
                Map.of("provider", "claude", "model", "claude-3-sonnet")
        );
        when(chatService.getAvailableModels()).thenReturn(models);

        mockMvc.perform(get("/api/chat/models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].provider").value("openai"));
    }

    @Test
    void testGetCurrentModel() throws Exception {
        when(chatService.getCurrentModel()).thenReturn(Map.of("provider", "openai", "model", "gpt-4"));

        mockMvc.perform(get("/api/chat/model/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider").value("openai"))
                .andExpect(jsonPath("$.model").value("gpt-4"));
    }
}
