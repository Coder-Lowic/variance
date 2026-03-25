package com.lowic.ai.service;

import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpeechToTextServiceTest {

    @InjectMocks
    private SpeechToTextService speechToTextService;

    @Mock
    private OkHttpClient mockHttpClient;

    @Mock
    private MultipartFile mockAudioFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTranscribeWithWhisper() throws IOException {
        // 模拟文件
        when(mockAudioFile.getOriginalFilename()).thenReturn("test.wav");
        when(mockAudioFile.getBytes()).thenReturn(new byte[]{0x00, 0x01, 0x02});

        // 模拟API响应
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(ResponseBody.create("{\"text\": \"Hello world\"}", MediaType.parse("application/json")));

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mock(Call.class));
        when(mock(Call.class).execute()).thenReturn(mockResponse);

        // 测试
        String result = speechToTextService.transcribe(mockAudioFile, "whisper");
        assertEquals("Hello world", result);
    }

    @Test
    void testTranscribeWithOllama() throws IOException {
        // 模拟文件
        when(mockAudioFile.getOriginalFilename()).thenReturn("test.wav");
        when(mockAudioFile.getBytes()).thenReturn(new byte[]{0x00, 0x01, 0x02});

        // 模拟API响应
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(ResponseBody.create("{\"response\": \"Hello world\"}", MediaType.parse("application/json")));

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mock(Call.class));
        when(mock(Call.class).execute()).thenReturn(mockResponse);

        // 测试
        String result = speechToTextService.transcribe(mockAudioFile, "ollama");
        assertEquals("Hello world", result);
    }

    @Test
    void testTranscribeWithUnsupportedModel() {
        assertThrows(IllegalArgumentException.class, () -> {
            speechToTextService.transcribe(mockAudioFile, "unsupported");
        });
    }

    @Test
    void testGetFileExtension() {
        // 测试有扩展名的文件
        assertEquals("wav", speechToTextService.getFileExtension("test.wav"));
        assertEquals("mp3", speechToTextService.getFileExtension("audio.mp3"));
        
        // 测试无扩展名的文件
        assertEquals("wav", speechToTextService.getFileExtension("test"));
        
        // 测试null文件名
        assertEquals("wav", speechToTextService.getFileExtension(null));
    }
}
