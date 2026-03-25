package com.lowic.ai.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
public class SpeechToTextService {

    private final OkHttpClient httpClient;
    private final Gson gson;
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024; // 25MB

    public SpeechToTextService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    // 用于测试的构造函数
    SpeechToTextService(OkHttpClient httpClient, Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;
    }

    public String transcribe(MultipartFile audioFile, String model) throws IOException {
        // 检查文件大小
        if (audioFile.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Audio file size exceeds the limit of 25MB");
        }
        
        String extension = getFileExtension(audioFile.getOriginalFilename());
        
        switch (model) {
            case "whisper":
                return transcribeWithWhisper(audioFile, extension);
            case "ollama":
                return transcribeWithOllama(audioFile, extension);
            default:
                throw new IllegalArgumentException("Unsupported speech model: " + model);
        }
    }

    private String transcribeWithWhisper(MultipartFile audioFile, String extension) throws IOException {
        // 这里使用 OpenAI Whisper API 作为示例
        // 实际项目中可以替换为本地部署的 Whisper 模型
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getProperty("OPENAI_API_KEY");
        }
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("OPENAI_API_KEY environment variable not set");
        }

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("model", "whisper-1")
                .addFormDataPart("file", "audio." + extension, 
                        RequestBody.create(audioFile.getBytes(), MediaType.parse("audio/" + extension)))
                .addFormDataPart("response_format", "json")
                .build();

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/audio/transcriptions")
                .header("Authorization", "Bearer " + apiKey)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response + ": " + response.body().string());
            }
            String responseBody = response.body().string();
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);
            return json.get("text").getAsString();
        }
    }

    private String transcribeWithOllama(MultipartFile audioFile, String extension) throws IOException {
        // 使用 Ollama 本地语音模型
        String baseUrl = System.getenv("OLLAMA_BASE_URL") != null 
                ? System.getenv("OLLAMA_BASE_URL") 
                : "http://localhost:11434";

        byte[] audioBytes = audioFile.getBytes();
        String base64Audio = Base64.getEncoder().encodeToString(audioBytes);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "whisper");
        requestBody.addProperty("prompt", "");
        requestBody.addProperty("audio", base64Audio);

        RequestBody body = RequestBody.create(
                gson.toJson(requestBody),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(baseUrl + "/api/chat")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response + ": " + response.body().string());
            }
            String responseBody = response.body().string();
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);
            return json.get("message").getAsJsonObject().get("content").getAsString();
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "wav";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "wav";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
}
