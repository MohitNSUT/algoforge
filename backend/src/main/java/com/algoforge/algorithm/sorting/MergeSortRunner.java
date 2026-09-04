package com.algoforge.algorithm.sorting;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class MergeSortRunner implements AlgorithmRunner<int[], int[]> {

    private int stepCount = 1;

    @Override
    public int[] execute(int[] input) {
        int[] arr = Arrays.copyOf(input, input.length);
        mergeSort(arr, 0, arr.length - 1);
        return arr;
    }

    private void mergeSort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        int[] L = new int[n1];
        int[] R = new int[n2];
        for (int i = 0; i < n1; ++i) L[i] = arr[left + i];
        for (int j = 0; j < n2; ++j) R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }
        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
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

        mergeSortWithSteps(arr, 0, arr.length - 1, steps);

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("COMPLETE")
                .description("Array is sorted")
                .state(Map.of("array", Arrays.copyOf(arr, arr.length)))
                .build());

        return steps;
    }

    private void mergeSortWithSteps(int[] arr, int left, int right, List<ExecutionStep> steps) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            
            steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("DIVIDE")
                .description(String.format("Dividing array at mid %d (left=%d, right=%d)", mid, left, right))
                .state(Map.of(
                        "array", Arrays.copyOf(arr, arr.length),
                        "activeRange", List.of(left, right),
                        "mid", mid
                ))
                .build());

            mergeSortWithSteps(arr, left, mid, steps);
            mergeSortWithSteps(arr, mid + 1, right, steps);
            mergeWithSteps(arr, left, mid, right, steps);
        }
    }

    private void mergeWithSteps(int[] arr, int left, int mid, int right, List<ExecutionStep> steps) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        int[] L = new int[n1];
        int[] R = new int[n2];
        for (int i = 0; i < n1; ++i) L[i] = arr[left + i];
        for (int j = 0; j < n2; ++j) R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("COMPARE")
                .description(String.format("Comparing L[%d]=%d and R[%d]=%d", i, L[i], j, R[j]))
                .state(Map.of(
                        "array", Arrays.copyOf(arr, arr.length),
                        "activeRange", List.of(left, right),
                        "comparingIndices", List.of(left + i, mid + 1 + j)
                ))
                .build());

            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            
            steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("MERGE_WRITE")
                .description(String.format("Writing %d to index %d", arr[k], k))
                .state(Map.of(
                        "array", Arrays.copyOf(arr, arr.length),
                        "activeRange", List.of(left, right),
                        "writtenIndex", k
                ))
                .build());
            
            k++;
        }
        while (i < n1) {
            arr[k] = L[i];
            
            steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("MERGE_REMAINING")
                .description(String.format("Writing remaining L element %d to index %d", arr[k], k))
                .state(Map.of(
                        "array", Arrays.copyOf(arr, arr.length),
                        "activeRange", List.of(left, right),
                        "writtenIndex", k
                ))
                .build());
            
            i++;
            k++;
        }
        while (j < n2) {
            arr[k] = R[j];
            
            steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("MERGE_REMAINING")
                .description(String.format("Writing remaining R element %d to index %d", arr[k], k))
                .state(Map.of(
                        "array", Arrays.copyOf(arr, arr.length),
                        "activeRange", List.of(left, right),
                        "writtenIndex", k
                ))
                .build());
                
            j++;
            k++;
        }
        
        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("MERGE_COMPLETE")
                .description(String.format("Merged segment [%d, %d]", left, right))
                .state(Map.of(
                        "array", Arrays.copyOf(arr, arr.length),
                        "activeRange", List.of(left, right)
                ))
                .build());
    }

    @Override
    public String getAlgorithmSlug() {
        return "merge-sort";
    }
}
