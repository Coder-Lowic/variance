package com.lowic.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowic.ai.service.DocumentGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

class DocumentControllerTest {

    @Mock
    private DocumentGeneratorService documentGeneratorService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new DocumentController(documentGeneratorService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGenerateDocument() throws Exception {
        when(documentGeneratorService.generateDocument(anyString(), anyString(), any())).thenReturn("Generated document content");

        Map<String, Object> request = new HashMap<>();
        request.put("documentType", "report");
        request.put("content", "Test content");
        request.put("parameters", new HashMap<String, Object>());

        mockMvc.perform(post("/api/documents/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Generated document content"));
    }

    @Test
    void testGenerateReport() throws Exception {
        when(documentGeneratorService.generateReport(anyString())).thenReturn("Generated report content");

        Map<String, Object> request = new HashMap<>();
        request.put("content", "Report content");

        mockMvc.perform(post("/api/documents/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Generated report content"));
    }

    @Test
    void testGenerateEmail() throws Exception {
        when(documentGeneratorService.generateEmail(anyString())).thenReturn("Generated email content");

        Map<String, Object> request = new HashMap<>();
        request.put("content", "Email content");

        mockMvc.perform(post("/api/documents/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Generated email content"));
    }

    @Test
    void testGenerateContract() throws Exception {
        when(documentGeneratorService.generateContract(anyString())).thenReturn("Generated contract content");

        Map<String, Object> request = new HashMap<>();
        request.put("content", "Contract content");

        mockMvc.perform(post("/api/documents/contract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Generated contract content"));
    }

    @Test
    void testGenerateResume() throws Exception {
        when(documentGeneratorService.generateResume(anyString())).thenReturn("Generated resume content");

        Map<String, Object> request = new HashMap<>();
        request.put("content", "Resume content");

        mockMvc.perform(post("/api/documents/resume")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Generated resume content"));
    }

    @Test
    void testGetSupportedDocumentTypes() throws Exception {
        List<String> types = Arrays.asList("report", "email", "contract", "resume");
        when(documentGeneratorService.getSupportedDocumentTypes()).thenReturn(types);

        mockMvc.perform(get("/api/documents/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0]").value("report"));
    }
}
