package com.lowic.ai.controller;

import com.lowic.ai.service.ChatService;
import com.lowic.ai.service.DocumentParserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "AI对话接口", description = "提供与大语言模型对话的能力")
@RestController
@RequestMapping("/api/ai/chat")
public class ChatController {

    private final ChatService chatService;
    private final DocumentParserService documentParserService;

    public ChatController(ChatService chatService, DocumentParserService documentParserService) {
        this.chatService = chatService;
        this.documentParserService = documentParserService;
    }

    @Operation(summary = "基础对话", description = "与大语言模型进行基础对话")
    @PostMapping
    public String chat(@RequestBody String message) {
        return chatService.chat(message);
    }

    @Operation(summary = "带系统提示的对话", description = "使用自定义系统提示词与大语言模型对话")
    @PostMapping("/system")
    public String chatWithSystemPrompt(
            @RequestParam String systemPrompt,
            @RequestBody String userMessage) {
        return chatService.chatWithSystemPrompt(systemPrompt, userMessage);
    }

    @Operation(summary = "带附件的对话", description = "上传附件并基于附件内容进行对话")
    @PostMapping(value = "/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String chatWithAttachment(
            @RequestParam String message,
            @RequestParam("file") MultipartFile file) throws IOException {
        String attachmentContent = documentParserService.parseDocument(file);
        String filename = file.getOriginalFilename();
        return chatService.chatWithAttachment(message, attachmentContent, filename);
    }

    @Operation(summary = "带附件和系统提示的对话", description = "上传附件并基于附件内容和自定义系统提示进行对话")
    @PostMapping(value = "/attachment/system", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String chatWithAttachmentAndSystemPrompt(
            @RequestParam String systemPrompt,
            @RequestParam String message,
            @RequestParam("file") MultipartFile file) throws IOException {
        String attachmentContent = documentParserService.parseDocument(file);
        String filename = file.getOriginalFilename();
        return chatService.chatWithAttachmentAndSystemPrompt(systemPrompt, message, attachmentContent, filename);
    }

    @Operation(summary = "带图片的对话", description = "上传图片并基于图片内容进行对话")
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String chatWithImage(
            @RequestParam String message,
            @RequestParam("file") MultipartFile file) throws IOException {
        String imageBase64 = documentParserService.parseDocument(file);
        String filename = file.getOriginalFilename();
        return chatService.chatWithImage(message, imageBase64, filename);
    }

    @Operation(summary = "带图片和系统提示的对话", description = "上传图片并基于图片内容和自定义系统提示进行对话")
    @PostMapping(value = "/image/system", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String chatWithImageAndSystemPrompt(
            @RequestParam String systemPrompt,
            @RequestParam String message,
            @RequestParam("file") MultipartFile file) throws IOException {
        String imageBase64 = documentParserService.parseDocument(file);
        String filename = file.getOriginalFilename();
        return chatService.chatWithImageAndSystemPrompt(systemPrompt, message, imageBase64, filename);
    }

    @Operation(summary = "获取支持的文件格式", description = "获取系统支持的附件文件格式列表")
    @GetMapping("/supported-formats")
    public Map<String, Object> getSupportedFormats() {
        Map<String, Object> result = new HashMap<>();
        result.put("formats", documentParserService.getSupportedFormats());
        result.put("description", "支持的文件格式：PDF、Word文档、Excel表格、纯文本、Markdown、图片(JPG/PNG/GIF/BMP/WebP)");
        return result;
    }

    @Operation(summary = "语音输入对话", description = "上传音频文件并基于语音内容进行对话")
    @PostMapping(value = "/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String chatWithVoice(
            @RequestParam(value = "model", defaultValue = "whisper") String model,
            @RequestParam("file") MultipartFile file) throws IOException {
        return chatService.chatWithVoice(file, model);
    }

    @Operation(summary = "带系统提示的语音输入对话", description = "上传音频文件并基于语音内容和自定义系统提示进行对话")
    @PostMapping(value = "/voice/system", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String chatWithVoiceAndSystemPrompt(
            @RequestParam String systemPrompt,
            @RequestParam(value = "model", defaultValue = "whisper") String model,
            @RequestParam("file") MultipartFile file) throws IOException {
        return chatService.chatWithVoiceAndSystemPrompt(systemPrompt, file, model);
    }

    @Operation(summary = "获取支持的语音模型", description = "获取系统支持的语音识别模型列表")
    @GetMapping("/supported-voice-models")
    public Map<String, Object> getSupportedVoiceModels() {
        Map<String, Object> result = new HashMap<>();
        result.put("models", Map.of(
                "whisper", "OpenAI Whisper 语音识别模型",
                "ollama", "Ollama 本地语音识别模型"
        ));
        result.put("description", "支持的语音模型：Whisper (在线)、Ollama (本地)");
        return result;
    }
}
