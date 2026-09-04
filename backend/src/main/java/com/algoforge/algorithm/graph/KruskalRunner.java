package com.algoforge.algorithm.graph;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class KruskalRunner implements AlgorithmRunner<KruskalRunner.Input, List<int[]>> {

    public static class Input {
        public int numNodes;
        // Edge format: [source, destination, weight]
        public List<int[]> edges;
    }

    private int find(int[] parent, int i) {
        if (parent[i] == i) return i;
        return parent[i] = find(parent, parent[i]);
    }

    private void union(int[] parent, int[] rank, int x, int y) {
        int xroot = find(parent, x);
        int yroot = find(parent, y);
        if (xroot != yroot) {
            if (rank[xroot] < rank[yroot]) parent[xroot] = yroot;
            else if (rank[xroot] > rank[yroot]) parent[yroot] = xroot;
            else {
                parent[yroot] = xroot;
                rank[xroot]++;
            }
        }
    }

    @Override
    public List<int[]> execute(Input input) {
        List<int[]> mst = new ArrayList<>();
        input.edges.sort(Comparator.comparingInt(a -> a[2]));

        int[] parent = new int[input.numNodes];
        int[] rank = new int[input.numNodes];
        for (int i = 0; i < input.numNodes; i++) {
            parent[i] = i;
            rank[i] = 0;
        }

        for (int[] edge : input.edges) {
            int u = edge[0];
            int v = edge[1];
            if (find(parent, u) != find(parent, v)) {
                mst.add(edge);
                union(parent, rank, u, v);
            }
            if (mst.size() == input.numNodes - 1) break;
        }
        return mst;
    }

    @Override
    public List<ExecutionStep> executeWithSteps(Input input) {
        List<ExecutionStep> steps = new ArrayList<>();
        List<int[]> mst = new ArrayList<>();
        int stepCount = 1;

        List<int[]> sortedEdges = new ArrayList<>(input.edges);
        sortedEdges.sort(Comparator.comparingInt(a -> a[2]));
        
        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INIT")
                .description("Sorted all edges by weight for Kruskal's algorithm.")
                .state(Map.of(
                        "mst", new ArrayList<>(mst),
                        "sortedEdges", sortedEdges
                ))
                .build());

        int[] parent = new int[input.numNodes];
        int[] rank = new int[input.numNodes];
        for (int i = 0; i < input.numNodes; i++) {
            parent[i] = i;
            rank[i] = 0;
        }

        for (int[] edge : sortedEdges) {
            int u = edge[0];
            int v = edge[1];
            int weight = edge[2];
            
            steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("EVALUATE_EDGE")
                    .description(String.format("Evaluating smallest available edge: %d -> %d (weight %d)", u, v, weight))
                    .state(Map.of(
                            "mst", new ArrayList<>(mst),
                            "activeEdge", edge
                    ))
                    .build());

            if (find(parent, u) != find(parent, v)) {
                mst.add(edge);
                union(parent, rank, u, v);
                
                steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("EDGE_ADDED")
                        .description(String.format("Edge %d -> %d does not form a cycle. Added to MST.", u, v))
                        .state(Map.of(
                                "mst", new ArrayList<>(mst),
                                "addedEdge", edge
                        ))
                        .build());
            } else {
                steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("CYCLE_DETECTED")
                        .description(String.format("Edge %d -> %d forms a cycle! Discarding edge.", u, v))
                        .state(Map.of(
                                "mst", new ArrayList<>(mst),
                                "discardedEdge", edge
                        ))
                        .build());
            }

            if (mst.size() == input.numNodes - 1) {
                steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("MST_COMPLETE")
                        .description("MST has V-1 edges. Terminating early.")
                        .state(Map.of("mst", new ArrayList<>(mst)))
                        .build());
                break;
            }
        }

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("COMPLETE")
                .description("Kruskal's Algorithm completed.")
                .state(Map.of("mst", new ArrayList<>(mst)))
                .build());

        return steps;
    }

    @Override
    public String getAlgorithmSlug() {
        return "kruskal";
    }
}
