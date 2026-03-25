package com.lowic.ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatServiceVoiceTest {

    @InjectMocks
    private ChatService chatService;

    @Mock
    private ModelManagerService mockModelManagerService;

    @Mock
    private SpeechToTextService mockSpeechToTextService;

    @Mock
    private ChatClient mockChatClient;

    @Mock
    private ChatClient.RequestPart mockRequestPart;

    @Mock
    private ChatClient.ResponsePart mockResponsePart;

    @Mock
    private ChatResponse mockChatResponse;

    @Mock
    private MultipartFile mockAudioFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 模拟ChatClient行为
        when(mockModelManagerService.getCurrentChatClient()).thenReturn(mockChatClient);
        when(mockChatClient.prompt()).thenReturn(mockRequestPart);
        when(mockRequestPart.user(anyString())).thenReturn(mockRequestPart);
        when(mockRequestPart.system(anyString())).thenReturn(mockRequestPart);
        when(mockRequestPart.call()).thenReturn(mockChatResponse);
        when(mockChatResponse.content()).thenReturn("Hello from AI");
    }

    @Test
    void testChatWithVoice() throws IOException {
        // 模拟语音转文本
        when(mockSpeechToTextService.transcribe(mockAudioFile, "whisper")).thenReturn("Hello AI");

        // 测试
        String result = chatService.chatWithVoice(mockAudioFile, "whisper");
        assertEquals("Hello from AI", result);

        // 验证调用
        verify(mockSpeechToTextService).transcribe(mockAudioFile, "whisper");
        verify(mockModelManagerService).getCurrentChatClient();
    }

    @Test
    void testChatWithVoiceAndSystemPrompt() throws IOException {
        // 模拟语音转文本
        when(mockSpeechToTextService.transcribe(mockAudioFile, "ollama")).thenReturn("Hello AI");

        // 测试
        String result = chatService.chatWithVoiceAndSystemPrompt("You are a helpful assistant", mockAudioFile, "ollama");
        assertEquals("Hello from AI", result);

        // 验证调用
        verify(mockSpeechToTextService).transcribe(mockAudioFile, "ollama");
        verify(mockModelManagerService).getCurrentChatClient();
    }

    @Test
    void testChatWithVoiceIOException() throws IOException {
        // 模拟IOException
        when(mockSpeechToTextService.transcribe(mockAudioFile, "whisper")).thenThrow(new IOException("Audio processing error"));

        // 测试
        assertThrows(IOException.class, () -> {
            chatService.chatWithVoice(mockAudioFile, "whisper");
        });
    }
}
