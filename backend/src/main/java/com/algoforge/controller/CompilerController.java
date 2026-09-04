package com.algoforge.controller;

import com.algoforge.dto.ApiResponse;
import com.algoforge.service.CompilerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/compiler")
@RequiredArgsConstructor
public class CompilerController {

    private final CompilerService compilerService;
    private final com.algoforge.service.AiAnalysisService aiAnalysisService;

    @PostMapping("/execute")
    public ApiResponse<Map<String, Object>> executeCode(@RequestBody Map<String, Object> payload) {
        String language = payload.get("language") != null ? payload.get("language").toString() : null;
        String version = payload.get("version") != null ? payload.get("version").toString() : null;
        String code = payload.get("code") != null ? payload.get("code").toString() : null;

        if (language == null || version == null || code == null) {
            return ApiResponse.error("Missing language, version, or code in payload");
        }

        Map<String, Object> result = compilerService.executeCode(language, version, code);
        return ApiResponse.success(result, "Code execution completed");
    }

    @PostMapping("/analyze")
    public ApiResponse<String> analyzeComplexity(@RequestBody Map<String, Object> payload) {
        String code = payload.get("code") != null ? payload.get("code").toString() : null;
        
        if (code == null || code.trim().isEmpty()) {
            return ApiResponse.error("Missing code to analyze");
        }
        
        String analysis = aiAnalysisService.analyzeComplexity(code);
        return ApiResponse.success(analysis, "Complexity analyzed successfully");
    }
}
