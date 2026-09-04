package com.algoforge.algorithm.searching;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BinarySearchRunner implements AlgorithmRunner<BinarySearchRunner.Input, Integer> {

    public static class Input {
        public int[] array;
        public int target;
    }

    @Override
    public Integer execute(Input input) {
        int[] arr = input.array;
        int target = input.target;
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == target) return mid;
            if (arr[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    @Override
    public List<ExecutionStep> executeWithSteps(Input input) {
        List<ExecutionStep> steps = new ArrayList<>();
        int[] arr = input.array;
        int target = input.target;
        int left = 0;
        int right = arr.length - 1;
        int stepCount = 1;

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INIT")
                .description("Starting Binary Search for target " + target)
                .state(Map.of(
                        "array", arr,
                        "left", left,
                        "right", right,
                        "target", target
                ))
                .build());

        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("CALCULATE_MID")
                .description(String.format("Calculated mid = %d. Value at mid = %d", mid, arr[mid]))
                .state(Map.of(
                        "array", arr,
                        "left", left,
                        "right", right,
                        "mid", mid,
                        "target", target
                ))
                .build());

            if (arr[mid] == target) {
                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("FOUND")
                    .description("Target " + target + " found at index " + mid)
                    .state(Map.of(
                            "array", arr,
                            "left", left,
                            "right", right,
                            "mid", mid,
                            "foundIndex", mid,
                            "target", target
                    ))
                    .build());
                return steps;
            }

            if (arr[mid] < target) {
                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("SEARCH_RIGHT")
                    .description(String.format("Value %d < Target %d. Searching right half.", arr[mid], target))
                    .state(Map.of(
                            "array", arr,
                            "left", left,
                            "right", right,
                            "mid", mid,
                            "target", target
                    ))
                    .build());
                left = mid + 1;
            } else {
                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("SEARCH_LEFT")
                    .description(String.format("Value %d > Target %d. Searching left half.", arr[mid], target))
                    .state(Map.of(
                            "array", arr,
                            "left", left,
                            "right", right,
                            "mid", mid,
                            "target", target
                    ))
                    .build());
                right = mid - 1;
            }
        }

        steps.add(ExecutionStep.builder()
            .step(stepCount++)
            .operation("NOT_FOUND")
            .description("Target " + target + " not found in array")
            .state(Map.of(
                    "array", arr,
                    "target", target
            ))
            .build());

        return steps;
    }

    @Override
    public String getAlgorithmSlug() {
        return "binary-search";
    }
}
