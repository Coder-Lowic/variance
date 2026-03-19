package com.lowic.ai.controller;

import com.lowic.ai.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI对话接口", description = "提供与大语言模型对话的能力")
@RestController
@RequestMapping("/api/ai/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
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
}
