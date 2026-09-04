package com.algoforge.algorithm.sorting;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class SelectionSortRunner implements AlgorithmRunner<int[], int[]> {

    @Override
    public int[] execute(int[] input) {
        int[] arr = Arrays.copyOf(input, input.length);
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int min_idx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[min_idx]) {
                    min_idx = j;
                }
            }
            int temp = arr[min_idx];
            arr[min_idx] = arr[i];
            arr[i] = temp;
        }
        return arr;
    }

    @Override
    public List<ExecutionStep> executeWithSteps(int[] input) {
        List<ExecutionStep> steps = new ArrayList<>();
        int[] arr = Arrays.copyOf(input, input.length);
        int n = arr.length;
        int stepCount = 1;

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INIT")
                .description("Initial array")
                .state(Map.of("array", Arrays.copyOf(arr, arr.length)))
                .build());

        for (int i = 0; i < n - 1; i++) {
            int min_idx = i;
            
            steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("START_PASS")
                .description(String.format("Finding minimum element for index %d", i))
                .state(Map.of(
                        "array", Arrays.copyOf(arr, arr.length),
                        "sortedBoundary", i,
                        "minIdx", min_idx
                ))
                .build());

            for (int j = i + 1; j < n; j++) {
                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("COMPARE")
                    .description(String.format("Comparing arr[%d]=%d with current min arr[%d]=%d", j, arr[j], min_idx, arr[min_idx]))
                    .state(Map.of(
                            "array", Arrays.copyOf(arr, arr.length),
                            "sortedBoundary", i,
                            "comparingIndices", List.of(j, min_idx),
                            "minIdx", min_idx
                    ))
                    .build());
                    
                if (arr[j] < arr[min_idx]) {
                    min_idx = j;
                    
                    steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("NEW_MIN")
                        .description(String.format("Found new minimum: %d at index %d", arr[min_idx], min_idx))
                        .state(Map.of(
                                "array", Arrays.copyOf(arr, arr.length),
                                "sortedBoundary", i,
                                "minIdx", min_idx
                        ))
                        .build());
                }
            }
            
            if (min_idx != i) {
                int temp = arr[min_idx];
                arr[min_idx] = arr[i];
                arr[i] = temp;
                
                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("SWAP")
                    .description(String.format("Swapped minimum element %d into position %d", arr[i], i))
                    .state(Map.of(
                            "array", Arrays.copyOf(arr, arr.length),
                            "sortedBoundary", i,
                            "activeIndices", List.of(i, min_idx),
                            "swapped", true
                    ))
                    .build());
            } else {
                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("NO_SWAP")
                    .description(String.format("Element at %d is already the minimum, no swap needed", i))
                    .state(Map.of(
                            "array", Arrays.copyOf(arr, arr.length),
                            "sortedBoundary", i,
                            "activeIndices", List.of(i)
                    ))
                    .build());
            }
        }
        
        steps.add(ExecutionStep.builder()
            .step(stepCount++)
            .operation("COMPLETE")
            .description("Array is sorted using Selection Sort")
            .state(Map.of("array", Arrays.copyOf(arr, arr.length)))
            .build());

        return steps;
    }

    @Override
    public String getAlgorithmSlug() {
        return "selection-sort";
    }
}
