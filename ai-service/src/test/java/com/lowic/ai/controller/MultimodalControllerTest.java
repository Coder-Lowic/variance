package com.lowic.ai.controller;

import com.lowic.ai.service.MultimodalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MultimodalControllerTest {

    @Mock
    private MultimodalService multimodalService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new MultimodalController(multimodalService)).build();
    }

    @Test
    void testAnalyzeImage() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());
        when(multimodalService.analyzeImage(any(), anyString())).thenReturn("Image description result");

        mockMvc.perform(multipart("/api/multimodal/image/analyze")
                        .file(imageFile)
                        .param("prompt", "Describe this image"))
                .andExpect(status().isOk())
                .andExpect(content().string("Image description result"));
    }

    @Test
    void testAnalyzeImageWithoutPrompt() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());
        when(multimodalService.analyzeImage(any(), anyString())).thenReturn("Image description result");

        mockMvc.perform(multipart("/api/multimodal/image/analyze")
                        .file(imageFile))
                .andExpect(status().isOk())
                .andExpect(content().string("Image description result"));
    }

    @Test
    void testAnalyzeVideo() throws Exception {
        MockMultipartFile videoFile = new MockMultipartFile("file", "test.mp4", "video/mp4", "test video".getBytes());
        when(multimodalService.analyzeVideo(any(), anyString())).thenReturn("Video description result");

        mockMvc.perform(multipart("/api/multimodal/video/analyze")
                        .file(videoFile)
                        .param("prompt", "Describe this video"))
                .andExpect(status().isOk())
                .andExpect(content().string("Video description result"));
    }

    @Test
    void testMultimodalQA() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());
        when(multimodalService.analyzeMultimodal(any(), anyString())).thenReturn("QA answer");

        mockMvc.perform(multipart("/api/multimodal/qa")
                        .file(imageFile)
                        .param("question", "What is in the image?"))
                .andExpect(status().isOk())
                .andExpect(content().string("QA answer"));
    }
}
