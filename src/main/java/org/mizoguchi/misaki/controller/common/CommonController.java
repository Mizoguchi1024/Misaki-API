package org.mizoguchi.misaki.controller.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.common.TtsRequest;
import org.mizoguchi.misaki.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
@Tag(name = "通用接口")
public class CommonController {
    private final FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping("/files")
    public Result<String> upload(MultipartFile file) {
        String url = fileService.uploadFile(file);

        return Result.success(url);
    }

    @Operation(summary = "文字转语音")
    @GetMapping("/tts")
    public Result<MultipartFile> textToSpeech(TtsRequest ttsRequest) {
        // TODO TTS功能
        return Result.success(null);
    }
}
