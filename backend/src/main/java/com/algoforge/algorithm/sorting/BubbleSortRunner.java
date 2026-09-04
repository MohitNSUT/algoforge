package com.algoforge.algorithm.sorting;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class BubbleSortRunner implements AlgorithmRunner<int[], int[]> {

    @Override
    public int[] execute(int[] input) {
        int[] arr = Arrays.copyOf(input, input.length);
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
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
            for (int j = 0; j < n - i - 1; j++) {
                steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("COMPARE")
                        .description(String.format("Comparing index %d and %d", j, j+1))
                        .state(Map.of(
                                "array", Arrays.copyOf(arr, arr.length),
                                "activeIndices", List.of(j, j+1)
                        ))
                        .build());

                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    
                    steps.add(ExecutionStep.builder()
                            .step(stepCount++)
                            .operation("SWAP")
                            .description(String.format("Swapped index %d and %d", j, j+1))
                            .state(Map.of(
                                    "array", Arrays.copyOf(arr, arr.length),
                                    "activeIndices", List.of(j, j+1),
                                    "swapped", true
                            ))
                            .build());
                }
            }
        }

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("COMPLETE")
                .description("Array is sorted")
                .state(Map.of("array", Arrays.copyOf(arr, arr.length)))
                .build());

        return steps;
    }

    @Override
    public String getAlgorithmSlug() {
        return "bubble-sort";
    }
}
