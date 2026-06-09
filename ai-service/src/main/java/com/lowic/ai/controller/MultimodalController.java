package com.lowic.ai.controller;

import com.lowic.ai.service.MultimodalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "多模态服务", description = "多模态数据处理相关的API接口")
@RestController
@RequestMapping("/api/multimodal")
public class MultimodalController {

    private final MultimodalService multimodalService;

    public MultimodalController(MultimodalService multimodalService) {
        this.multimodalService = multimodalService;
    }

    @Operation(summary = "分析图像", description = "分析图像内容，生成图像描述")
    @PostMapping("/image/analyze")
    public ResponseEntity<String> analyzeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "prompt", required = false) String prompt) throws IOException {
        if (prompt == null) {
            prompt = "请详细描述这张图片的内容，包括物体、场景、颜色、人物等所有可见元素";
        }
        String description = multimodalService.analyzeImage(file, prompt);
        return ResponseEntity.ok(description);
    }

    @Operation(summary = "分析视频", description = "分析视频内容，生成视频描述")
    @PostMapping("/video/analyze")
    public ResponseEntity<String> analyzeVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "prompt", required = false) String prompt) throws IOException {
        if (prompt == null) {
            prompt = "请详细描述这个视频的内容，包括场景、人物、动作、情节等";
        }
        String description = multimodalService.analyzeVideo(file, prompt);
        return ResponseEntity.ok(description);
    }

    @Operation(summary = "多模态问答", description = "基于图像和问题进行问答")
    @PostMapping("/qa")
    public ResponseEntity<String> multimodalQA(
            @RequestParam("file") MultipartFile file,
            @RequestParam("question") String question) throws IOException {
        String answer = multimodalService.analyzeMultimodal(file, null, question);
        return ResponseEntity.ok(answer);
    }
}
