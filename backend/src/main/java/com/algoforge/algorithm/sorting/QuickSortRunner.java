package com.algoforge.algorithm.sorting;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class QuickSortRunner implements AlgorithmRunner<int[], int[]> {

    private int stepCount = 1;

    @Override
    public int[] execute(int[] input) {
        int[] arr = Arrays.copyOf(input, input.length);
        quickSort(arr, 0, arr.length - 1);
        return arr;
    }

    private void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    @Override
    public List<ExecutionStep> executeWithSteps(int[] input) {
        List<ExecutionStep> steps = new ArrayList<>();
        int[] arr = Arrays.copyOf(input, input.length);
        stepCount = 1;

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INIT")
                .description("Initial array")
                .state(Map.of("array", Arrays.copyOf(arr, arr.length)))
                .build());

        quickSortWithSteps(arr, 0, arr.length - 1, steps);

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("COMPLETE")
                .description("Array is sorted using Quick Sort")
                .state(Map.of("array", Arrays.copyOf(arr, arr.length)))
                .build());

        return steps;
    }

    private void quickSortWithSteps(int[] arr, int low, int high, List<ExecutionStep> steps) {
        if (low < high) {
            int pi = partitionWithSteps(arr, low, high, steps);
            
            steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("PIVOT_SET")
                .description(String.format("Pivot placed at its correct sorted position: %d", pi))
                .state(Map.of(
                        "array", Arrays.copyOf(arr, arr.length),
                        "activeRange", List.of(low, high),
                        "pivotIndex", pi
                ))
                .build());
                
            quickSortWithSteps(arr, low, pi - 1, steps);
            quickSortWithSteps(arr, pi + 1, high, steps);
        }
    }

    private int partitionWithSteps(int[] arr, int low, int high, List<ExecutionStep> steps) {
        int pivot = arr[high];
        int i = (low - 1);
        
        steps.add(ExecutionStep.builder()
            .step(stepCount++)
            .operation("CHOOSE_PIVOT")
            .description(String.format("Choosing pivot element: %d at index %d", pivot, high))
            .state(Map.of(
                    "array", Arrays.copyOf(arr, arr.length),
                    "activeRange", List.of(low, high),
                    "pivotIndex", high
            ))
            .build());
            
        for (int j = low; j < high; j++) {
            steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("COMPARE")
                .description(String.format("Comparing arr[%d]=%d with pivot %d", j, arr[j], pivot))
                .state(Map.of(
                        "array", Arrays.copyOf(arr, arr.length),
                        "activeRange", List.of(low, high),
                        "comparingIndices", List.of(j, high),
                        "pivotIndex", high
                ))
                .build());
                
            if (arr[j] < pivot) {
                i++;
                if (i != j) {
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                    
                    steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("SWAP")
                        .description(String.format("Element %d < pivot, swapping with arr[%d]=%d", arr[i], i, arr[j]))
                        .state(Map.of(
                                "array", Arrays.copyOf(arr, arr.length),
                                "activeRange", List.of(low, high),
                                "activeIndices", List.of(i, j),
                                "pivotIndex", high,
                                "swapped", true
                        ))
                        .build());
                }
            }
        }
        
        if (i + 1 != high) {
            int temp = arr[i + 1];
            arr[i + 1] = arr[high];
            arr[high] = temp;
            
            steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("SWAP_PIVOT")
                .description(String.format("Swapping pivot %d into correct position at %d", arr[i+1], i+1))
                .state(Map.of(
                        "array", Arrays.copyOf(arr, arr.length),
                        "activeRange", List.of(low, high),
                        "activeIndices", List.of(i + 1, high),
                        "pivotIndex", i + 1,
                        "swapped", true
                ))
                .build());
        }
        
        return i + 1;
    }

    @Override
    public String getAlgorithmSlug() {
        return "quick-sort";
    }
}
