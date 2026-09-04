package com.algoforge.algorithm.graph;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FloydWarshallRunner implements AlgorithmRunner<FloydWarshallRunner.Input, int[][]> {

    public static class Input {
        public int numNodes;
        // graph is an adjacency matrix where graph[i][j] is the weight of edge i -> j
        // Use Integer.MAX_VALUE to represent infinity (no edge)
        public int[][] graph;
    }

    private final int INF = 99999; // Safe infinity value to prevent overflow during addition

    @Override
    public int[][] execute(Input input) {
        int V = input.numNodes;
        int[][] dist = new int[V][V];

        for (int i = 0; i < V; i++)
            for (int j = 0; j < V; j++) {
                dist[i][j] = (input.graph[i][j] == Integer.MAX_VALUE) ? INF : input.graph[i][j];
            }

        for (int k = 0; k < V; k++) {
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        return dist;
    }

    @Override
    public List<ExecutionStep> executeWithSteps(Input input) {
        List<ExecutionStep> steps = new ArrayList<>();
        int V = input.numNodes;
        int[][] dist = new int[V][V];
        int stepCount = 1;

        for (int i = 0; i < V; i++)
            for (int j = 0; j < V; j++) {
                dist[i][j] = (input.graph[i][j] == Integer.MAX_VALUE) ? INF : input.graph[i][j];
            }

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INIT")
                .description("Initializing distance matrix with given graph weights")
                .state(Map.of(
                        "matrix", copyMatrix(dist),
                        "k", -1
                ))
                .build());

        for (int k = 0; k < V; k++) {
            steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("NEW_INTERMEDIATE")
                    .description("Using node " + k + " as intermediate node for all pairs")
                    .state(Map.of(
                            "matrix", copyMatrix(dist),
                            "k", k
                    ))
                    .build());

            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    steps.add(ExecutionStep.builder()
                            .step(stepCount++)
                            .operation("COMPARE")
                            .description(String.format("Comparing dist[%d][%d] with dist[%d][%d] + dist[%d][%d]", i, j, i, k, k, j))
                            .state(Map.of(
                                    "matrix", copyMatrix(dist),
                                    "k", k,
                                    "i", i,
                                    "j", j
                            ))
                            .build());

                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        
                        steps.add(ExecutionStep.builder()
                                .step(stepCount++)
                                .operation("UPDATE")
                                .description(String.format("Updating dist[%d][%d] to %d", i, j, dist[i][j]))
                                .state(Map.of(
                                        "matrix", copyMatrix(dist),
                                        "k", k,
                                        "i", i,
                                        "j", j,
                                        "updated", true
                                ))
                                .build());
                    }
                }
            }
        }

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("COMPLETE")
                .description("Floyd-Warshall Algorithm completed.")
                .state(Map.of(
                        "matrix", copyMatrix(dist)
                ))
                .build());

        return steps;
    }

    private int[][] copyMatrix(int[][] matrix) {
        int[][] copy = new int[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, matrix.length);
        }
        return copy;
    }

    @Override
    public String getAlgorithmSlug() {
        return "floyd-warshall";
    }
}
