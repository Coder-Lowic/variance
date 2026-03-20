package com.lowic.ai.controller;

import com.lowic.ai.model.ModelConfig;
import com.lowic.ai.model.ModelProvider;
import com.lowic.ai.service.ModelManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "模型管理接口", description = "大模型提供商管理和切换")
@RestController
@RequestMapping("/api/ai/model")
public class ModelController {

    private final ModelManagerService modelManagerService;

    public ModelController(ModelManagerService modelManagerService) {
        this.modelManagerService = modelManagerService;
    }

    @Operation(summary = "获取当前模型配置", description = "获取当前使用的模型提供商和配置")
    @GetMapping("/current")
    public Map<String, Object> getCurrentModel() {
        Map<String, Object> result = new HashMap<>();
        ModelConfig config = modelManagerService.getCurrentConfig();
        result.put("provider", config.getProvider().getCode());
        result.put("providerName", config.getProvider().getName());
        result.put("modelName", config.getModelName());
        result.put("temperature", config.getTemperature());
        result.put("maxTokens", config.getMaxTokens());
        return result;
    }

    @Operation(summary = "获取可用模型列表", description = "获取所有支持的模型提供商和可用模型")
    @GetMapping("/available")
    public Map<String, Object> getAvailableModels() {
        Map<String, Object> result = new HashMap<>();
        result.put("providers", modelManagerService.getAvailableModels());
        return result;
    }

    @Operation(summary = "切换到OpenAI模型", description = "切换到OpenAI GPT系列模型")
    @PostMapping("/switch/openai")
    public Map<String, Object> switchToOpenAI(@RequestParam(required = false, defaultValue = "gpt-4") String modelName) {
        ModelConfig config = ModelConfig.defaultOpenAI();
        config.setModelName(modelName);
        modelManagerService.switchModel(config);
        return getCurrentModel();
    }

    @Operation(summary = "切换到Anthropic Claude模型", description = "切换到Anthropic Claude系列模型")
    @PostMapping("/switch/anthropic")
    public Map<String, Object> switchToAnthropic(@RequestParam(required = false, defaultValue = "claude-3-opus-20240229") String modelName) {
        ModelConfig config = ModelConfig.defaultAnthropic();
        config.setModelName(modelName);
        modelManagerService.switchModel(config);
        return getCurrentModel();
    }

    @Operation(summary = "切换到Google Gemini模型", description = "切换到Google Gemini系列模型")
    @PostMapping("/switch/gemini")
    public Map<String, Object> switchToGemini(@RequestParam(required = false, defaultValue = "gemini-pro") String modelName) {
        ModelConfig config = ModelConfig.defaultGemini();
        config.setModelName(modelName);
        modelManagerService.switchModel(config);
        return getCurrentModel();
    }

    @Operation(summary = "切换到Ollama本地模型", description = "切换到Ollama本地模型")
    @PostMapping("/switch/ollama")
    public Map<String, Object> switchToOllama(
            @RequestParam(required = false, defaultValue = "llama3") String modelName,
            @RequestParam(required = false, defaultValue = "http://localhost:11434") String baseUrl) {
        ModelConfig config = ModelConfig.defaultOllama();
        config.setModelName(modelName);
        config.setBaseUrl(baseUrl);
        modelManagerService.switchModel(config);
        return getCurrentModel();
    }

    @Operation(summary = "自定义模型切换", description = "使用自定义配置切换模型")
    @PostMapping("/switch/custom")
    public Map<String, Object> switchToCustomModel(@RequestBody ModelConfig config) {
        modelManagerService.switchModel(config);
        return getCurrentModel();
    }
}
