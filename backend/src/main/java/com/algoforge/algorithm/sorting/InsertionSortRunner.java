package com.algoforge.algorithm.sorting;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class InsertionSortRunner implements AlgorithmRunner<int[], int[]> {

    @Override
    public int[] execute(int[] input) {
        int[] arr = Arrays.copyOf(input, input.length);
        int n = arr.length;
        for (int i = 1; i < n; ++i) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = key;
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

        for (int i = 1; i < n; ++i) {
            int key = arr[i];
            int j = i - 1;
            
            steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("SELECT_KEY")
                .description(String.format("Selected key %d at index %d to insert into sorted portion", key, i))
                .state(Map.of(
                        "array", Arrays.copyOf(arr, arr.length),
                        "keyIndex", i,
                        "key", key
                ))
                .build());

            while (j >= 0 && arr[j] > key) {
                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("COMPARE")
                    .description(String.format("Comparing arr[%d]=%d with key %d", j, arr[j], key))
                    .state(Map.of(
                            "array", Arrays.copyOf(arr, arr.length),
                            "comparingIndices", List.of(j, j + 1),
                            "keyIndex", i,
                            "key", key
                    ))
                    .build());
                    
                arr[j + 1] = arr[j];
                
                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("SHIFT")
                    .description(String.format("Shifted %d to the right", arr[j]))
                    .state(Map.of(
                            "array", Arrays.copyOf(arr, arr.length),
                            "activeIndices", List.of(j + 1),
                            "keyIndex", i,
                            "key", key
                    ))
                    .build());
                    
                j = j - 1;
            }
            
            if (j >= 0) {
                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("COMPARE")
                    .description(String.format("Comparing arr[%d]=%d with key %d. No shift needed.", j, arr[j], key))
                    .state(Map.of(
                            "array", Arrays.copyOf(arr, arr.length),
                            "comparingIndices", List.of(j),
                            "keyIndex", i,
                            "key", key
                    ))
                    .build());
            }

            arr[j + 1] = key;
            
            steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INSERT_KEY")
                .description(String.format("Inserted key %d at index %d", key, j + 1))
                .state(Map.of(
                        "array", Arrays.copyOf(arr, arr.length),
                        "insertedIndex", j + 1,
                        "key", key
                ))
                .build());
        }
        
        steps.add(ExecutionStep.builder()
            .step(stepCount++)
            .operation("COMPLETE")
            .description("Array is sorted using Insertion Sort")
            .state(Map.of("array", Arrays.copyOf(arr, arr.length)))
            .build());

        return steps;
    }

    @Override
    public String getAlgorithmSlug() {
        return "insertion-sort";
    }
}
