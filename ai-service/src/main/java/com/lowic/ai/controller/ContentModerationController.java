package com.lowic.ai.controller;

import com.lowic.ai.entity.ContentModerationResult;
import com.lowic.ai.service.ContentModerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "内容审核", description = "内容审核相关的API接口，包括文本、图片、视频审核等")
@RestController
@RequestMapping("/api/moderation")
public class ContentModerationController {

    private final ContentModerationService contentModerationService;

    public ContentModerationController(ContentModerationService contentModerationService) {
        this.contentModerationService = contentModerationService;
    }

    @Operation(summary = "文本内容审核", description = "对文本内容进行AI审核，检测违规内容")
    @PostMapping("/text")
    public ResponseEntity<ContentModerationResult> moderateText(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        String contentId = request.getOrDefault("contentId", UUID.randomUUID().toString());
        ContentModerationResult result = contentModerationService.moderateText(content, contentId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "图片内容审核", description = "对图片内容进行AI审核")
    @PostMapping("/image")
    public ResponseEntity<ContentModerationResult> moderateImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "contentId", required = false) String contentId) {
        try {
            if (contentId == null) {
                contentId = UUID.randomUUID().toString();
            }
            ContentModerationResult result = contentModerationService.moderateImage(file, contentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            ContentModerationResult errorResult = new ContentModerationResult();
            errorResult.setContentId(contentId);
            errorResult.setSafe(false);
            errorResult.setOverallRiskLevel("high");
            errorResult.setDetails(Map.of("error", e.getMessage()));
            return ResponseEntity.badRequest().body(errorResult);
        }
    }

    @Operation(summary = "视频内容审核", description = "对视频内容进行AI审核")
    @PostMapping("/video")
    public ResponseEntity<ContentModerationResult> moderateVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "contentId", required = false) String contentId) {
        try {
            if (contentId == null) {
                contentId = UUID.randomUUID().toString();
            }
            ContentModerationResult result = contentModerationService.moderateVideo(file, contentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            ContentModerationResult errorResult = new ContentModerationResult();
            errorResult.setContentId(contentId);
            errorResult.setSafe(false);
            errorResult.setOverallRiskLevel("high");
            errorResult.setDetails(Map.of("error", e.getMessage()));
            return ResponseEntity.badRequest().body(errorResult);
        }
    }

    @Operation(summary = "敏感信息过滤", description = "检测并过滤文本中的敏感信息（手机号、邮箱、身份证号等）")
    @PostMapping("/sensitive")
    public ResponseEntity<ContentModerationResult> filterSensitiveInfo(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        String contentId = request.getOrDefault("contentId", UUID.randomUUID().toString());
        ContentModerationResult result = contentModerationService.filterSensitiveInfo(content, contentId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "批量内容审核", description = "对多个文本内容进行批量审核")
    @PostMapping("/batch")
    public ResponseEntity<List<ContentModerationResult>> batchModerate(@RequestBody List<String> contents) {
        List<ContentModerationResult> results = contentModerationService.batchModerate(contents);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "综合内容审核", description = "对内容进行全面审核，包括AI审核、敏感信息检测等")
    @PostMapping("/comprehensive")
    public ResponseEntity<ContentModerationResult> comprehensiveModerate(
            @RequestParam("content") String content,
            @RequestParam(value = "contentId", required = false) String contentId,
            @RequestParam(value = "checkSensitiveInfo", defaultValue = "true") boolean checkSensitiveInfo,
            @RequestParam(value = "checkAI", defaultValue = "true") boolean checkAI,
            @RequestParam(value = "checkImage", defaultValue = "false") boolean checkImage,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            if (contentId == null) {
                contentId = UUID.randomUUID().toString();
            }
            ContentModerationResult result = contentModerationService.comprehensiveModeration(
                    content, contentId, checkSensitiveInfo, checkAI, checkImage, imageFile);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            ContentModerationResult errorResult = new ContentModerationResult();
            errorResult.setContentId(contentId);
            errorResult.setSafe(false);
            errorResult.setOverallRiskLevel("high");
            errorResult.setDetails(Map.of("error", e.getMessage()));
            return ResponseEntity.badRequest().body(errorResult);
        }
    }

    @Operation(summary = "快速检查内容安全", description = "快速检查内容是否安全，返回布尔值")
    @PostMapping("/quick-check")
    public ResponseEntity<Map<String, Object>> quickCheck(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        String contentId = request.getOrDefault("contentId", UUID.randomUUID().toString());
        ContentModerationResult result = contentModerationService.moderateText(content, contentId);
        
        return ResponseEntity.ok(Map.of(
                "isSafe", result.isSafe(),
                "riskLevel", result.getOverallRiskLevel(),
                "contentId", contentId
        ));
    }
}
