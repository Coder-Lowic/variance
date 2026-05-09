package com.lowic.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowic.ai.service.RagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.document.Document;
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

class RagControllerTest {

    @Mock
    private RagService ragService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new RagController(ragService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testAddText() throws Exception {
        when(ragService.addText(anyString(), any())).thenReturn(1);

        Map<String, Object> request = new HashMap<>();
        request.put("content", "Test text content");
        request.put("metadata", new HashMap<String, Object>());

        mockMvc.perform(post("/api/rag/text")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void testAddImage() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());
        when(ragService.addImage(any(), any())).thenReturn(1);

        mockMvc.perform(multipart("/api/rag/image")
                        .file(imageFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void testAddVideo() throws Exception {
        MockMultipartFile videoFile = new MockMultipartFile("file", "test.mp4", "video/mp4", "test video".getBytes());
        when(ragService.addVideo(any(), any())).thenReturn(1);

        mockMvc.perform(multipart("/api/rag/video")
                        .file(videoFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void testAddDocument() throws Exception {
        MockMultipartFile docFile = new MockMultipartFile("file", "test.txt", "text/plain", "test document".getBytes());
        when(ragService.addDocument(any())).thenReturn(1);

        mockMvc.perform(multipart("/api/rag/document")
                        .file(docFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void testSearchDocuments() throws Exception {
        List<Document> documents = Arrays.asList(
                new Document("doc1", "Content 1"),
                new Document("doc2", "Content 2")
        );
        when(ragService.searchDocuments(anyString(), anyInt())).thenReturn(documents);

        mockMvc.perform(get("/api/rag/search")
                        .param("query", "test")
                        .param("k", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testRagQuery() throws Exception {
        when(ragService.ragQuery(anyString(), anyInt())).thenReturn("RAG answer");

        Map<String, Object> request = new HashMap<>();
        request.put("prompt", "What is AI?");
        request.put("k", 3);

        mockMvc.perform(post("/api/rag/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("RAG answer"));
    }

    @Test
    void testDeleteDocument() throws Exception {
        when(ragService.deleteDocument(anyString())).thenReturn(true);

        mockMvc.perform(delete("/api/rag/document/test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetDocumentCount() throws Exception {
        when(ragService.getDocumentCount()).thenReturn(10L);

        mockMvc.perform(get("/api/rag/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(10));
    }

    @Test
    void testClearDocuments() throws Exception {
        mockMvc.perform(delete("/api/rag/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
