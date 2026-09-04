package com.algoforge.algorithm.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionStep {
    private int step;
    private String operation;
    private String description;
    
    // Using a map to flexibly store state (e.g., active indices for arrays, current node for graphs)
    private Map<String, Object> state;
}
