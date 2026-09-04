package com.algoforge.algorithm.graph;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BellmanFordRunner implements AlgorithmRunner<BellmanFordRunner.Input, Map<String, Object>> {

    public static class Input {
        public int numNodes;
        public int startNode;
        // Edge format: [source, destination, weight]
        public List<int[]> edges;
    }

    @Override
    public Map<String, Object> execute(Input input) {
        int[] dist = new int[input.numNodes];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[input.startNode] = 0;

        for (int i = 1; i < input.numNodes; ++i) {
            for (int[] edge : input.edges) {
                int u = edge[0];
                int v = edge[1];
                int weight = edge[2];
                if (dist[u] != Integer.MAX_VALUE && dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                }
            }
        }

        boolean hasNegativeCycle = false;
        for (int[] edge : input.edges) {
            int u = edge[0];
            int v = edge[1];
            int weight = edge[2];
            if (dist[u] != Integer.MAX_VALUE && dist[u] + weight < dist[v]) {
                hasNegativeCycle = true;
                break;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("distances", dist);
        result.put("hasNegativeCycle", hasNegativeCycle);
        return result;
    }

    @Override
    public List<ExecutionStep> executeWithSteps(Input input) {
        List<ExecutionStep> steps = new ArrayList<>();
        int[] dist = new int[input.numNodes];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[input.startNode] = 0;
        int stepCount = 1;

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INIT")
                .description("Initializing distances to infinity, start node to 0")
                .state(Map.of(
                        "distances", Arrays.copyOf(dist, dist.length)
                ))
                .build());

        for (int i = 1; i < input.numNodes; ++i) {
            steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("ITERATION_START")
                    .description("Starting relaxation iteration " + i)
                    .state(Map.of(
                            "distances", Arrays.copyOf(dist, dist.length),
                            "iteration", i
                    ))
                    .build());

            for (int[] edge : input.edges) {
                int u = edge[0];
                int v = edge[1];
                int weight = edge[2];
                
                steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("CHECK_EDGE")
                        .description(String.format("Checking edge %d -> %d (weight %d)", u, v, weight))
                        .state(Map.of(
                                "distances", Arrays.copyOf(dist, dist.length),
                                "activeEdge", List.of(u, v)
                        ))
                        .build());

                if (dist[u] != Integer.MAX_VALUE && dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    
                    steps.add(ExecutionStep.builder()
                            .step(stepCount++)
                            .operation("RELAX_EDGE")
                            .description(String.format("Relaxing edge %d -> %d. New distance: %d", u, v, dist[v]))
                            .state(Map.of(
                                    "distances", Arrays.copyOf(dist, dist.length),
                                    "activeEdge", List.of(u, v),
                                    "relaxed", true
                            ))
                            .build());
                }
            }
        }

        boolean hasNegativeCycle = false;
        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("CHECK_CYCLE")
                .description("Checking for negative weight cycles")
                .state(Map.of(
                        "distances", Arrays.copyOf(dist, dist.length)
                ))
                .build());

        for (int[] edge : input.edges) {
            int u = edge[0];
            int v = edge[1];
            int weight = edge[2];
            if (dist[u] != Integer.MAX_VALUE && dist[u] + weight < dist[v]) {
                hasNegativeCycle = true;
                
                steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("NEGATIVE_CYCLE_FOUND")
                        .description(String.format("Negative cycle detected at edge %d -> %d", u, v))
                        .state(Map.of(
                                "distances", Arrays.copyOf(dist, dist.length),
                                "activeEdge", List.of(u, v),
                                "hasNegativeCycle", true
                        ))
                        .build());
                break;
            }
        }

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("COMPLETE")
                .description("Bellman-Ford Algorithm completed.")
                .state(Map.of(
                        "distances", Arrays.copyOf(dist, dist.length),
                        "hasNegativeCycle", hasNegativeCycle
                ))
                .build());

        return steps;
    }

    @Override
    public String getAlgorithmSlug() {
        return "bellman-ford";
    }
}
