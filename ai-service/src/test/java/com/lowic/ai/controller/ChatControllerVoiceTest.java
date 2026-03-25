package com.lowic.ai.controller;

import com.lowic.ai.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ChatControllerVoiceTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ChatController chatController;

    @Mock
    private ChatService mockChatService;

    @Mock
    private com.lowic.ai.service.DocumentParserService mockDocumentParserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
    }

    @Test
    void testChatWithVoice() throws Exception {
        // 模拟音频文件
        MockMultipartFile audioFile = new MockMultipartFile(
                "file",
                "test.wav",
                "audio/wav",
                new byte[]{0x00, 0x01, 0x02}
        );

        // 模拟服务响应
        when(mockChatService.chatWithVoice(any(), eq("whisper"))).thenReturn("Hello from AI");

        // 测试POST请求
        mockMvc.perform(multipart("/api/ai/chat/voice")
                .file(audioFile)
                .param("model", "whisper")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from AI"));

        // 验证调用
        verify(mockChatService).chatWithVoice(any(), eq("whisper"));
    }

    @Test
    void testChatWithVoiceAndSystemPrompt() throws Exception {
        // 模拟音频文件
        MockMultipartFile audioFile = new MockMultipartFile(
                "file",
                "test.wav",
                "audio/wav",
                new byte[]{0x00, 0x01, 0x02}
        );

        // 模拟服务响应
        when(mockChatService.chatWithVoiceAndSystemPrompt(eq("You are a helpful assistant"), any(), eq("ollama"))).thenReturn("Hello from AI");

        // 测试POST请求
        mockMvc.perform(multipart("/api/ai/chat/voice/system")
                .file(audioFile)
                .param("systemPrompt", "You are a helpful assistant")
                .param("model", "ollama")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from AI"));

        // 验证调用
        verify(mockChatService).chatWithVoiceAndSystemPrompt(eq("You are a helpful assistant"), any(), eq("ollama"));
    }

    @Test
    void testChatWithVoiceIOException() throws Exception {
        // 模拟音频文件
        MockMultipartFile audioFile = new MockMultipartFile(
                "file",
                "test.wav",
                "audio/wav",
                new byte[]{0x00, 0x01, 0x02}
        );

        // 模拟IOException
        when(mockChatService.chatWithVoice(any(), eq("whisper"))).thenThrow(new IOException("Audio processing error"));

        // 测试POST请求
        mockMvc.perform(multipart("/api/ai/chat/voice")
                .file(audioFile)
                .param("model", "whisper")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetSupportedVoiceModels() throws Exception {
        // 测试GET请求
        mockMvc.perform(get("/api/ai/chat/supported-voice-models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.models.whisper").value("OpenAI Whisper 语音识别模型"))
                .andExpect(jsonPath("$.models.ollama").value("Ollama 本地语音识别模型"))
                .andExpect(jsonPath("$.description").value("支持的语音模型：Whisper (在线)、Ollama (本地)"));
    }
}
