package com.algoforge.algorithm.graph;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UnionFindRunner implements AlgorithmRunner<UnionFindRunner.Input, int[]> {

    public static class Input {
        public int numNodes;
        // List of union operations [u, v]
        public List<int[]> operations;
    }

    private int find(int[] parent, int i) {
        if (parent[i] == i)
            return i;
        return parent[i] = find(parent, parent[i]); // Path compression
    }

    private void union(int[] parent, int[] rank, int x, int y) {
        int xroot = find(parent, x);
        int yroot = find(parent, y);

        if (xroot != yroot) {
            if (rank[xroot] < rank[yroot])
                parent[xroot] = yroot;
            else if (rank[xroot] > rank[yroot])
                parent[yroot] = xroot;
            else {
                parent[yroot] = xroot;
                rank[xroot]++;
            }
        }
    }

    @Override
    public int[] execute(Input input) {
        int[] parent = new int[input.numNodes];
        int[] rank = new int[input.numNodes];
        for (int i = 0; i < input.numNodes; i++) {
            parent[i] = i;
            rank[i] = 0;
        }

        for (int[] op : input.operations) {
            union(parent, rank, op[0], op[1]);
        }
        
        // Final pass for path compression
        for (int i = 0; i < input.numNodes; i++) {
            find(parent, i);
        }
        return parent;
    }

    @Override
    public List<ExecutionStep> executeWithSteps(Input input) {
        List<ExecutionStep> steps = new ArrayList<>();
        int stepCount = 1;

        int[] parent = new int[input.numNodes];
        int[] rank = new int[input.numNodes];
        for (int i = 0; i < input.numNodes; i++) {
            parent[i] = i;
            rank[i] = 0;
        }

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INIT")
                .description("Initialized disjoint sets. Every node is its own parent.")
                .state(Map.of(
                        "parent", Arrays.copyOf(parent, parent.length),
                        "rank", Arrays.copyOf(rank, rank.length)
                ))
                .build());

        for (int[] op : input.operations) {
            int u = op[0];
            int v = op[1];
            
            steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("UNION_OP")
                    .description(String.format("Performing Union(%d, %d)", u, v))
                    .state(Map.of(
                            "activeNodes", List.of(u, v),
                            "parent", Arrays.copyOf(parent, parent.length),
                            "rank", Arrays.copyOf(rank, rank.length)
                    ))
                    .build());

            int rootU = findWithSteps(parent, u, steps, stepCount++);
            int rootV = findWithSteps(parent, v, steps, stepCount++);
            stepCount = steps.get(steps.size()-1).getStep() + 1; // Sync stepCount

            if (rootU != rootV) {
                if (rank[rootU] < rank[rootV]) {
                    parent[rootU] = rootV;
                    steps.add(ExecutionStep.builder()
                            .step(stepCount++)
                            .operation("UNION_SUCCESS")
                            .description(String.format("Union successful. Parent of root %d is now %d (by rank)", rootU, rootV))
                            .state(Map.of(
                                    "activeNodes", List.of(u, v),
                                    "parent", Arrays.copyOf(parent, parent.length),
                                    "rank", Arrays.copyOf(rank, rank.length)
                            ))
                            .build());
                } else if (rank[rootU] > rank[rootV]) {
                    parent[rootV] = rootU;
                    steps.add(ExecutionStep.builder()
                            .step(stepCount++)
                            .operation("UNION_SUCCESS")
                            .description(String.format("Union successful. Parent of root %d is now %d (by rank)", rootV, rootU))
                            .state(Map.of(
                                    "activeNodes", List.of(u, v),
                                    "parent", Arrays.copyOf(parent, parent.length),
                                    "rank", Arrays.copyOf(rank, rank.length)
                            ))
                            .build());
                } else {
                    parent[rootV] = rootU;
                    rank[rootU]++;
                    steps.add(ExecutionStep.builder()
                            .step(stepCount++)
                            .operation("UNION_SUCCESS")
                            .description(String.format("Union successful. Parent of root %d is now %d. Rank of %d increased.", rootV, rootU, rootU))
                            .state(Map.of(
                                    "activeNodes", List.of(u, v),
                                    "parent", Arrays.copyOf(parent, parent.length),
                                    "rank", Arrays.copyOf(rank, rank.length)
                            ))
                            .build());
                }
            } else {
                steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("CYCLE_DETECTED")
                        .description(String.format("Nodes %d and %d already have same root %d. Cycle detected/redundant union.", u, v, rootU))
                        .state(Map.of(
                                "activeNodes", List.of(u, v),
                                "parent", Arrays.copyOf(parent, parent.length),
                                "rank", Arrays.copyOf(rank, rank.length)
                        ))
                        .build());
            }
        }

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("COMPLETE")
                .description("Union-Find operations completed.")
                .state(Map.of(
                        "parent", Arrays.copyOf(parent, parent.length),
                        "rank", Arrays.copyOf(rank, rank.length)
                ))
                .build());

        return steps;
    }
    
    private int findWithSteps(int[] parent, int i, List<ExecutionStep> steps, int stepCount) {
        List<Integer> path = new ArrayList<>();
        int curr = i;
        while (parent[curr] != curr) {
            path.add(curr);
            curr = parent[curr];
        }
        
        int root = curr;
        if (!path.isEmpty()) {
            steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("FIND_ROOT")
                    .description(String.format("Finding root for node %d. Root is %d.", i, root))
                    .state(Map.of(
                            "activeNode", i,
                            "root", root,
                            "parent", Arrays.copyOf(parent, parent.length)
                    ))
                    .build());
            
            // Path compression
            for (int node : path) {
                if (parent[node] != root) {
                    parent[node] = root;
                    steps.add(ExecutionStep.builder()
                            .step(stepCount++)
                            .operation("PATH_COMPRESSION")
                            .description(String.format("Path compression: Parent of node %d set directly to root %d.", node, root))
                            .state(Map.of(
                                    "activeNode", node,
                                    "root", root,
                                    "parent", Arrays.copyOf(parent, parent.length)
                            ))
                            .build());
                }
            }
        }
        return root;
    }

    @Override
    public String getAlgorithmSlug() {
        return "union-find";
    }
}
