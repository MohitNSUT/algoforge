package com.algoforge.algorithm.core;

import java.util.List;

public interface AlgorithmRunner<T, R> {
    
    // Execute and just return the result
    R execute(T input);
    
    // Execute and return the step-by-step state
    List<ExecutionStep> executeWithSteps(T input);
    
    String getAlgorithmSlug();
}
