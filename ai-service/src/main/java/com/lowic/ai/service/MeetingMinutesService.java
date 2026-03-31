package com.lowic.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class MeetingMinutesService {

    private final SpeechToTextService speechToTextService;
    private final ChatClient chatClient;

    public MeetingMinutesService(SpeechToTextService speechToTextService, ChatClient chatClient) {
        this.speechToTextService = speechToTextService;
        this.chatClient = chatClient;
    }

    /**
     * 将会议录音转换为会议纪要
     * @param audioFile 会议录音文件
     * @param model 语音转文本模型（whisper 或 ollama）
     * @return 会议纪要
     * @throws IOException 处理音频文件时可能发生的异常
     */
    public String generateMeetingMinutes(MultipartFile audioFile, String model) throws IOException {
        // 1. 将音频文件转换为文本
        String transcription = speechToTextService.transcribe(audioFile, model);
        
        // 2. 使用大模型将转录文本转换为会议纪要
        return generateMinutesFromTranscription(transcription);
    }

    /**
     * 从转录文本生成会议纪要
     * @param transcription 会议录音的转录文本
     * @return 会议纪要
     */
    private String generateMinutesFromTranscription(String transcription) {
        String prompt = String.format("""
                请根据以下会议录音转录文本，生成一份详细的会议纪要：
                
                %s
                
                会议纪要应包括：
                1. 会议基本信息（日期、时间、参与人员）
                2. 会议议程
                3. 讨论的主要内容
                4. 达成的共识
                5. 行动项（包括负责人和截止日期）
                6. 下次会议安排
                
                请以正式、清晰的格式输出会议纪要。
                """, transcription);
        
        return chatClient.prompt()
                .system("你是一个专业的会议纪要生成助手，能够从会议录音转录文本中提取关键信息，生成结构清晰、内容完整的会议纪要。")
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 生成会议纪要（支持系统提示自定义）
     * @param audioFile 会议录音文件
     * @param model 语音转文本模型（whisper 或 ollama）
     * @param systemPrompt 自定义系统提示
     * @return 会议纪要
     * @throws IOException 处理音频文件时可能发生的异常
     */
    public String generateMeetingMinutesWithSystemPrompt(MultipartFile audioFile, String model, String systemPrompt) throws IOException {
        // 1. 将音频文件转换为文本
        String transcription = speechToTextService.transcribe(audioFile, model);
        
        // 2. 使用自定义系统提示将转录文本转换为会议纪要
        String prompt = String.format("""
                请根据以下会议录音转录文本，生成一份详细的会议纪要：
                
                %s
                
                会议纪要应包括：
                1. 会议基本信息（日期、时间、参与人员）
                2. 会议议程
                3. 讨论的主要内容
                4. 达成的共识
                5. 行动项（包括负责人和截止日期）
                6. 下次会议安排
                
                请以正式、清晰的格式输出会议纪要。
                """, transcription);
        
        return chatClient.prompt()
                .system(systemPrompt)
                .user(prompt)
                .call()
                .content();
    }
}
