package com.algoforge.algorithm.graph;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BfsRunner implements AlgorithmRunner<BfsRunner.Input, List<Integer>> {

    public static class Input {
        public int numNodes;
        public Map<Integer, List<Integer>> adjacencyList;
        public int startNode;
    }

    @Override
    public List<Integer> execute(Input input) {
        List<Integer> result = new ArrayList<>();
        boolean[] visited = new boolean[input.numNodes];
        Queue<Integer> queue = new LinkedList<>();

        visited[input.startNode] = true;
        queue.add(input.startNode);

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            result.add(curr);

            for (int neighbor : input.adjacencyList.getOrDefault(curr, new ArrayList<>())) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
        return result;
    }

    @Override
    public List<ExecutionStep> executeWithSteps(Input input) {
        List<ExecutionStep> steps = new ArrayList<>();
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        int stepCount = 1;

        visited.add(input.startNode);
        queue.add(input.startNode);
        
        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INIT")
                .description("Initializing BFS. Start node: " + input.startNode)
                .state(Map.of(
                        "visited", new HashSet<>(visited),
                        "queue", new ArrayList<>(queue),
                        "result", new ArrayList<>(result)
                ))
                .build());

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            result.add(curr);
            
            steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("VISIT_NODE")
                .description("Dequeued and visiting node: " + curr)
                .state(Map.of(
                        "currentNode", curr,
                        "visited", new HashSet<>(visited),
                        "queue", new ArrayList<>(queue),
                        "result", new ArrayList<>(result)
                ))
                .build());

            for (int neighbor : input.adjacencyList.getOrDefault(curr, new ArrayList<>())) {
                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("CHECK_NEIGHBOR")
                    .description(String.format("Checking neighbor %d of node %d", neighbor, curr))
                    .state(Map.of(
                            "currentNode", curr,
                            "neighbor", neighbor,
                            "visited", new HashSet<>(visited),
                            "queue", new ArrayList<>(queue),
                            "result", new ArrayList<>(result)
                    ))
                    .build());
                    
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    
                    steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("ENQUEUE_NEIGHBOR")
                        .description(String.format("Node %d is unvisited. Marking visited and adding to queue.", neighbor))
                        .state(Map.of(
                                "currentNode", curr,
                                "neighbor", neighbor,
                                "visited", new HashSet<>(visited),
                                "queue", new ArrayList<>(queue),
                                "result", new ArrayList<>(result)
                        ))
                        .build());
                } else {
                    steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("SKIP_NEIGHBOR")
                        .description(String.format("Node %d is already visited. Skipping.", neighbor))
                        .state(Map.of(
                                "currentNode", curr,
                                "neighbor", neighbor,
                                "visited", new HashSet<>(visited),
                                "queue", new ArrayList<>(queue),
                                "result", new ArrayList<>(result)
                        ))
                        .build());
                }
            }
        }
        
        steps.add(ExecutionStep.builder()
            .step(stepCount++)
            .operation("COMPLETE")
            .description("BFS Algorithm completed.")
            .state(Map.of(
                    "visited", new HashSet<>(visited),
                    "queue", new ArrayList<>(queue),
                    "result", new ArrayList<>(result)
            ))
            .build());

        return steps;
    }

    @Override
    public String getAlgorithmSlug() {
        return "bfs";
    }
}
