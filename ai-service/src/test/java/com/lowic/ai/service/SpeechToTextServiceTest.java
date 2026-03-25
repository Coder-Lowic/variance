package com.lowic.ai.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SpeechToTextServiceTest {

    @Mock
    private OkHttpClient mockHttpClient;

    @Mock
    private Call mockCall;

    @Mock
    private Response mockResponse;

    @Mock
    private ResponseBody mockResponseBody;

    private SpeechToTextService speechToTextService;

    private final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        speechToTextService = new SpeechToTextService(mockHttpClient, gson);
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("OPENAI_API_KEY");
    }

    @Test
    void testTranscribeWithWhisper() throws IOException {
        // 模拟音频文件
        MultipartFile mockAudioFile = org.mockito.Mockito.mock(MultipartFile.class);
        byte[] audioData = "test audio data".getBytes();
        InputStream inputStream = new ByteArrayInputStream(audioData);

        when(mockAudioFile.getInputStream()).thenReturn(inputStream);
        when(mockAudioFile.getOriginalFilename()).thenReturn("test.wav");
        when(mockAudioFile.getSize()).thenReturn((long) audioData.length);
        when(mockAudioFile.getBytes()).thenReturn(audioData);

        // 设置环境变量
        System.setProperty("OPENAI_API_KEY", "test-api-key");

        // 模拟HTTP响应
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("text", "Whisper transcription result");

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(gson.toJson(jsonResponse));

        // 测试transcribe方法（使用whisper模型）
        String result = speechToTextService.transcribe(mockAudioFile, "whisper");

        // 验证结果
        assertEquals("Whisper transcription result", result);
    }

    @Test
    void testTranscribeWithOllama() throws IOException {
        // 模拟音频文件
        MultipartFile mockAudioFile = org.mockito.Mockito.mock(MultipartFile.class);
        byte[] audioData = "test audio data".getBytes();
        InputStream inputStream = new ByteArrayInputStream(audioData);

        when(mockAudioFile.getInputStream()).thenReturn(inputStream);
        when(mockAudioFile.getOriginalFilename()).thenReturn("test.wav");
        when(mockAudioFile.getSize()).thenReturn((long) audioData.length);
        when(mockAudioFile.getBytes()).thenReturn(audioData);

        // 模拟HTTP响应
        JsonObject messageObject = new JsonObject();
        messageObject.addProperty("content", "Ollama transcription result");
        
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("message", messageObject);

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(gson.toJson(jsonResponse));

        // 测试transcribe方法（使用ollama模型）
        String result = speechToTextService.transcribe(mockAudioFile, "ollama");

        // 验证结果
        assertEquals("Ollama transcription result", result);
    }

    @Test
    void testTranscribeWithUnsupportedModel() throws IOException {
        // 模拟音频文件
        MultipartFile mockAudioFile = org.mockito.Mockito.mock(MultipartFile.class);
        when(mockAudioFile.getOriginalFilename()).thenReturn("test.wav");
        when(mockAudioFile.getSize()).thenReturn(100L);

        // 测试使用不支持的模型
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            speechToTextService.transcribe(mockAudioFile, "unsupported");
        });

        // 验证异常信息
        assertEquals("Unsupported speech model: unsupported", exception.getMessage());
    }

    @Test
    void testTranscribeWithNullFilename() throws IOException {
        // 模拟音频文件
        MultipartFile mockAudioFile = org.mockito.Mockito.mock(MultipartFile.class);
        when(mockAudioFile.getOriginalFilename()).thenReturn(null);
        when(mockAudioFile.getSize()).thenReturn(100L);

        // 测试null文件名 - 应该抛出RuntimeException因为OPENAI_API_KEY未设置
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            speechToTextService.transcribe(mockAudioFile, "whisper");
        });

        // 验证异常信息
        assertEquals("OPENAI_API_KEY environment variable not set", exception.getMessage());
    }

    @Test
    void testTranscribeWithFileSizeExceeded() throws IOException {
        // 模拟音频文件 - 大小超过25MB
        MultipartFile mockAudioFile = org.mockito.Mockito.mock(MultipartFile.class);
        when(mockAudioFile.getOriginalFilename()).thenReturn("test.wav");
        when(mockAudioFile.getSize()).thenReturn(26L * 1024 * 1024); // 26MB

        // 测试文件大小超过限制
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            speechToTextService.transcribe(mockAudioFile, "whisper");
        });

        // 验证异常信息
        assertEquals("Audio file size exceeds the limit of 25MB", exception.getMessage());
    }

    @Test
    void testTranscribeWithWhisperApiError() throws IOException {
        // 模拟音频文件
        MultipartFile mockAudioFile = org.mockito.Mockito.mock(MultipartFile.class);
        byte[] audioData = "test audio data".getBytes();
        InputStream inputStream = new ByteArrayInputStream(audioData);

        when(mockAudioFile.getInputStream()).thenReturn(inputStream);
        when(mockAudioFile.getOriginalFilename()).thenReturn("test.wav");
        when(mockAudioFile.getSize()).thenReturn((long) audioData.length);
        when(mockAudioFile.getBytes()).thenReturn(audioData);

        // 设置环境变量
        System.setProperty("OPENAI_API_KEY", "test-api-key");

        // 模拟HTTP错误响应
        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockResponse.code()).thenReturn(401);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn("{\"error\": {\"message\": \"Invalid API key\"}}");

        // 测试API错误
        IOException exception = assertThrows(IOException.class, () -> {
            speechToTextService.transcribe(mockAudioFile, "whisper");
        });

        // 验证异常信息包含错误详情
        assertTrue(exception.getMessage().contains("Unexpected code"));
        assertTrue(exception.getMessage().contains("Invalid API key"));
    }
}
