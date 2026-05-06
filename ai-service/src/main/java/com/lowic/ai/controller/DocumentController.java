package com.lowic.ai.controller;

import com.lowic.ai.service.DocumentGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "文档生成", description = "文档生成相关的API接口")
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentGeneratorService documentGeneratorService;

    public DocumentController(DocumentGeneratorService documentGeneratorService) {
        this.documentGeneratorService = documentGeneratorService;
    }

    @Operation(summary = "生成文档", description = "根据文档类型和内容生成文档")
    @PostMapping("/generate")
    public ResponseEntity<String> generateDocument(@RequestBody Map<String, Object> request) {
        String documentType = (String) request.get("documentType");
        String content = (String) request.get("content");
        @SuppressWarnings("unchecked")
        Map<String, Object> parameters = (Map<String, Object>) request.get("parameters");
        String document = documentGeneratorService.generateDocument(documentType, content, parameters);
        return ResponseEntity.ok(document);
    }

    @Operation(summary = "生成报告", description = "生成专业报告文档")
    @PostMapping("/report")
    public ResponseEntity<String> generateReport(@RequestBody Map<String, Object> request) {
        String content = (String) request.get("content");
        String report = documentGeneratorService.generateReport(content);
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "生成邮件", description = "生成专业邮件")
    @PostMapping("/email")
    public ResponseEntity<String> generateEmail(@RequestBody Map<String, Object> request) {
        String content = (String) request.get("content");
        String email = documentGeneratorService.generateEmail(content);
        return ResponseEntity.ok(email);
    }

    @Operation(summary = "生成合同", description = "生成合同文档")
    @PostMapping("/contract")
    public ResponseEntity<String> generateContract(@RequestBody Map<String, Object> request) {
        String content = (String) request.get("content");
        String contract = documentGeneratorService.generateContract(content);
        return ResponseEntity.ok(contract);
    }

    @Operation(summary = "生成简历", description = "生成简历文档")
    @PostMapping("/resume")
    public ResponseEntity<String> generateResume(@RequestBody Map<String, Object> request) {
        String content = (String) request.get("content");
        String resume = documentGeneratorService.generateResume(content);
        return ResponseEntity.ok(resume);
    }

    @Operation(summary = "获取支持的文档类型", description = "获取系统支持的所有文档类型")
    @GetMapping("/types")
    public ResponseEntity<List<String>> getSupportedDocumentTypes() {
        List<String> types = documentGeneratorService.getSupportedDocumentTypes();
        return ResponseEntity.ok(types);
    }
}
