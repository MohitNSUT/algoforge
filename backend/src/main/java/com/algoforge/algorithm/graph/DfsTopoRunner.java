package com.algoforge.algorithm.graph;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DfsTopoRunner implements AlgorithmRunner<DfsTopoRunner.Input, List<Integer>> {

    public static class Input {
        public int numNodes;
        public Map<Integer, List<Integer>> adjacencyList;
    }

    private int stepCount = 1;

    @Override
    public List<Integer> execute(Input input) {
        boolean[] visited = new boolean[input.numNodes];
        List<Integer> result = new ArrayList<>();
        
        for (int i = 0; i < input.numNodes; i++) {
            if (!visited[i]) {
                dfs(i, input.adjacencyList, visited, result);
            }
        }
        Collections.reverse(result);
        return result;
    }
    
    private void dfs(int node, Map<Integer, List<Integer>> adj, boolean[] visited, List<Integer> result) {
        visited[node] = true;
        for (int neighbor : adj.getOrDefault(node, new ArrayList<>())) {
            if (!visited[neighbor]) {
                dfs(neighbor, adj, visited, result);
            }
        }
        result.add(node);
    }

    @Override
    public List<ExecutionStep> executeWithSteps(Input input) {
        List<ExecutionStep> steps = new ArrayList<>();
        stepCount = 1;
        
        boolean[] visited = new boolean[input.numNodes];
        boolean[] inStack = new boolean[input.numNodes];
        List<Integer> result = new ArrayList<>();
        
        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INIT")
                .description("Initializing DFS Topological Sort")
                .state(Map.of(
                        "visited", copyBooleanArray(visited),
                        "topoSort", new ArrayList<>(result)
                ))
                .build());

        boolean hasCycle = false;
        for (int i = 0; i < input.numNodes; i++) {
            if (!visited[i]) {
                steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("NEW_DFS_TREE")
                        .description(String.format("Starting DFS from unvisited node %d", i))
                        .state(Map.of(
                                "visited", copyBooleanArray(visited),
                                "topoSort", new ArrayList<>(result)
                        ))
                        .build());
                        
                if (dfsWithSteps(i, input.adjacencyList, visited, inStack, result, steps)) {
                    hasCycle = true;
                    break;
                }
            }
        }
        
        if (!hasCycle) {
            steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("REVERSE_POSTORDER")
                    .description("DFS finished. Reversing the post-order sequence to get Topological Sort.")
                    .state(Map.of(
                            "visited", copyBooleanArray(visited),
                            "topoSort", new ArrayList<>(result)
                    ))
                    .build());
                    
            Collections.reverse(result);
        }

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("COMPLETE")
                .description(hasCycle ? "Algorithm terminated. Cycle detected!" : "DFS Topological Sort completed.")
                .state(Map.of(
                        "visited", copyBooleanArray(visited),
                        "topoSort", new ArrayList<>(result),
                        "hasCycle", hasCycle
                ))
                .build());

        return steps;
    }
    
    private boolean dfsWithSteps(int node, Map<Integer, List<Integer>> adj, boolean[] visited, boolean[] inStack, List<Integer> result, List<ExecutionStep> steps) {
        visited[node] = true;
        inStack[node] = true;
        
        steps.add(ExecutionStep.builder()
            .step(stepCount++)
            .operation("VISIT_NODE")
            .description("Visiting node: " + node)
            .state(Map.of(
                    "currentNode", node,
                    "visited", copyBooleanArray(visited),
                    "topoSort", new ArrayList<>(result)
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
                        "visited", copyBooleanArray(visited),
                        "topoSort", new ArrayList<>(result)
                ))
                .build());
                
            if (!visited[neighbor]) {
                if (dfsWithSteps(neighbor, adj, visited, inStack, result, steps)) {
                    return true;
                }
            } else if (inStack[neighbor]) {
                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("CYCLE_DETECTED")
                    .description(String.format("Back-edge detected from %d to %d (node is in current recursion stack). Cycle exists!", node, neighbor))
                    .state(Map.of(
                            "currentNode", node,
                            "neighbor", neighbor,
                            "visited", copyBooleanArray(visited),
                            "topoSort", new ArrayList<>(result),
                            "hasCycle", true
                    ))
                    .build());
                return true;
            } else {
                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("SKIP_NEIGHBOR")
                    .description(String.format("Node %d is already visited and processed. Skipping.", neighbor))
                    .state(Map.of(
                            "currentNode", node,
                            "neighbor", neighbor,
                            "visited", copyBooleanArray(visited),
                            "topoSort", new ArrayList<>(result)
                    ))
                    .build());
            }
        }
        
        inStack[node] = false;
        result.add(node);
        
        steps.add(ExecutionStep.builder()
            .step(stepCount++)
            .operation("POSTORDER_ADD")
            .description(String.format("Finished processing node %d and all its descendants. Added to post-order list.", node))
            .state(Map.of(
                    "currentNode", node,
                    "visited", copyBooleanArray(visited),
                    "topoSort", new ArrayList<>(result)
            ))
            .build());
            
        return false;
    }
    
    private List<Integer> copyBooleanArray(boolean[] arr) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i]) list.add(i);
        }
        return list;
    }

    @Override
    public String getAlgorithmSlug() {
        return "dfs-topo";
    }
}
