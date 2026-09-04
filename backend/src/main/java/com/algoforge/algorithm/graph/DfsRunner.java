package com.algoforge.algorithm.graph;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DfsRunner implements AlgorithmRunner<DfsRunner.Input, List<Integer>> {

    public static class Input {
        public int numNodes;
        public Map<Integer, List<Integer>> adjacencyList;
        public int startNode;
    }

    private int stepCount = 1;

    @Override
    public List<Integer> execute(Input input) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        dfs(input.startNode, input.adjacencyList, visited, result);
        return result;
    }
    
    private void dfs(int node, Map<Integer, List<Integer>> adj, Set<Integer> visited, List<Integer> result) {
        visited.add(node);
        result.add(node);
        for (int neighbor : adj.getOrDefault(node, new ArrayList<>())) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, adj, visited, result);
            }
        }
    }

    @Override
    public List<ExecutionStep> executeWithSteps(Input input) {
        List<ExecutionStep> steps = new ArrayList<>();
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        stepCount = 1;
        
        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INIT")
                .description("Initializing DFS. Start node: " + input.startNode)
                .state(Map.of(
                        "visited", new HashSet<>(visited),
                        "result", new ArrayList<>(result)
                ))
                .build());

        dfsWithSteps(input.startNode, input.adjacencyList, visited, result, steps, null);
        
        steps.add(ExecutionStep.builder()
            .step(stepCount++)
            .operation("COMPLETE")
            .description("DFS Algorithm completed.")
            .state(Map.of(
                    "visited", new HashSet<>(visited),
                    "result", new ArrayList<>(result)
            ))
            .build());

        return steps;
    }
    
    private void dfsWithSteps(int node, Map<Integer, List<Integer>> adj, Set<Integer> visited, List<Integer> result, List<ExecutionStep> steps, Integer parent) {
        visited.add(node);
        result.add(node);
        
        steps.add(ExecutionStep.builder()
            .step(stepCount++)
            .operation("VISIT_NODE")
            .description("Visiting node: " + node)
            .state(Map.of(
                    "currentNode", node,
                    "visited", new HashSet<>(visited),
                    "result", new ArrayList<>(result)
            ))
            .build());

        for (int neighbor : adj.getOrDefault(node, new ArrayList<>())) {
            steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("CHECK_NEIGHBOR")
                .description(String.format("Checking neighbor %d of node %d", neighbor, node))
                .state(Map.of(
                        "currentNode", node,
                        "neighbor", neighbor,
                        "visited", new HashSet<>(visited),
                        "result", new ArrayList<>(result)
                ))
                .build());
                
            if (!visited.contains(neighbor)) {
                dfsWithSteps(neighbor, adj, visited, result, steps, node);
                
                // Backtrack step
                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("BACKTRACK")
                    .description(String.format("Backtracking to node %d from %d", node, neighbor))
                    .state(Map.of(
                            "currentNode", node,
                            "visited", new HashSet<>(visited),
                            "result", new ArrayList<>(result)
                    ))
                    .build());
            } else {
                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("SKIP_NEIGHBOR")
                    .description(String.format("Node %d is already visited. Skipping.", neighbor))
                    .state(Map.of(
                            "currentNode", node,
                            "neighbor", neighbor,
                            "visited", new HashSet<>(visited),
                            "result", new ArrayList<>(result)
                    ))
                    .build());
            }
        }
    }

    @Override
    public String getAlgorithmSlug() {
        return "dfs";
    }
}
