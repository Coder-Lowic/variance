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

    @Operation(summary = "获取支持的文件格式", description = "获取系统支持的附件文件格式列表")
    @GetMapping("/supported-formats")
    public Map<String, Object> getSupportedFormats() {
        Map<String, Object> result = new HashMap<>();
        result.put("formats", documentParserService.getSupportedFormats());
        result.put("description", "支持的文件格式：PDF、Word文档、Excel表格、纯文本、Markdown");
        return result;
    }
}
