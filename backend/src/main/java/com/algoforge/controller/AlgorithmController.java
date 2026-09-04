package com.algoforge.controller;

import com.algoforge.algorithm.core.ExecutionStep;
import com.algoforge.dto.ApiResponse;
import com.algoforge.service.AlgorithmExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/algorithms")
@RequiredArgsConstructor
public class AlgorithmController {

    private final AlgorithmExecutionService algorithmExecutionService;

    @PostMapping("/{slug}/execute")
    public ApiResponse<List<ExecutionStep>> executeAlgorithm(
            @PathVariable String slug,
            @RequestBody Map<String, Object> payload) {
        
        List<ExecutionStep> steps = algorithmExecutionService.executeAlgorithmSteps(slug, payload);
        return ApiResponse.success(steps, "Algorithm executed successfully");
    }
}
