package com.lowic.ai.controller;

import com.lowic.ai.service.MeetingMinutesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "会议纪要生成", description = "基于会议录音生成会议纪要的功能")
@RestController
@RequestMapping("/api/meeting")
public class MeetingMinutesController {

    private final MeetingMinutesService meetingMinutesService;

    public MeetingMinutesController(MeetingMinutesService meetingMinutesService) {
        this.meetingMinutesService = meetingMinutesService;
    }

    @Operation(summary = "生成会议纪要", description = "上传会议录音文件，生成会议纪要")
    @PostMapping("/minutes")
    public ResponseEntity<String> generateMeetingMinutes(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "model", defaultValue = "whisper") String model) throws IOException {
        String minutes = meetingMinutesService.generateMeetingMinutes(file, model);
        return ResponseEntity.ok(minutes);
    }

    @Operation(summary = "生成会议纪要（自定义系统提示）", description = "上传会议录音文件，使用自定义系统提示生成会议纪要")
    @PostMapping("/minutes/custom")
    public ResponseEntity<String> generateMeetingMinutesWithSystemPrompt(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "model", defaultValue = "whisper") String model,
            @RequestParam("systemPrompt") String systemPrompt) throws IOException {
        String minutes = meetingMinutesService.generateMeetingMinutesWithSystemPrompt(file, model, systemPrompt);
        return ResponseEntity.ok(minutes);
    }
}
