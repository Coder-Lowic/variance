package com.lowic.ai.controller;

import com.lowic.ai.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Tag(name = "对话服务", description = "AI对话相关的API接口")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "发送消息", description = "向AI发送消息并获取回复")
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String response = chatService.chat(message);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "发送消息（带系统提示）", description = "使用自定义系统提示向AI发送消息")
    @PostMapping("/send/system")
    public ResponseEntity<String> sendMessageWithSystemPrompt(@RequestBody Map<String, String> request) {
        String systemPrompt = request.get("systemPrompt");
        String userMessage = request.get("message");
        String response = chatService.chatWithSystemPrompt(systemPrompt, userMessage);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "RAG增强对话", description = "基于检索增强生成的对话")
    @PostMapping("/rag")
    public ResponseEntity<String> chatWithRAG(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        int k = request.containsKey("k") ? (Integer) request.get("k") : 3;
        String response = chatService.chatWithRAG(message, k);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "图像对话", description = "上传图像并进行问答")
    @PostMapping("/image")
    public ResponseEntity<String> chatWithImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("prompt") String prompt,
            @RequestParam(value = "k", defaultValue = "3") int k) throws IOException {
        String response = chatService.chatWithRAGAndImage(file, prompt, k);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "切换模型", description = "切换当前使用的AI模型")
    @PostMapping("/model")
    public ResponseEntity<Map<String, Object>> switchModel(@RequestBody Map<String, String> request) {
        String provider = request.get("provider");
        String model = request.get("model");
        chatService.switchModel(provider, model);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "已切换到 " + provider + " 模型"
        ));
    }

    @Operation(summary = "获取可用模型列表", description = "获取当前支持的所有AI模型")
    @GetMapping("/models")
    public ResponseEntity<List<Map<String, String>>> getAvailableModels() {
        List<Map<String, String>> models = chatService.getAvailableModels();
        return ResponseEntity.ok(models);
    }

    @Operation(summary = "获取当前模型", description = "获取当前正在使用的模型")
    @GetMapping("/model/current")
    public ResponseEntity<Map<String, String>> getCurrentModel() {
        Map<String, String> currentModel = chatService.getCurrentModel();
        return ResponseEntity.ok(currentModel);
    }
}
