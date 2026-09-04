package com.algoforge.algorithm.graph;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class KahnRunner implements AlgorithmRunner<KahnRunner.Input, List<Integer>> {

    public static class Input {
        public int numNodes;
        public Map<Integer, List<Integer>> adjacencyList;
    }

    @Override
    public List<Integer> execute(Input input) {
        int[] inDegree = new int[input.numNodes];
        for (int u = 0; u < input.numNodes; u++) {
            for (int v : input.adjacencyList.getOrDefault(u, new ArrayList<>())) {
                inDegree[v]++;
            }
        }

        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < input.numNodes; i++) {
            if (inDegree[i] == 0) q.add(i);
        }

        List<Integer> topoSort = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            topoSort.add(u);
            for (int v : input.adjacencyList.getOrDefault(u, new ArrayList<>())) {
                inDegree[v]--;
                if (inDegree[v] == 0) q.add(v);
            }
        }
        return topoSort;
    }

    @Override
    public List<ExecutionStep> executeWithSteps(Input input) {
        List<ExecutionStep> steps = new ArrayList<>();
        int stepCount = 1;
        
        int[] inDegree = new int[input.numNodes];
        for (int u = 0; u < input.numNodes; u++) {
            for (int v : input.adjacencyList.getOrDefault(u, new ArrayList<>())) {
                inDegree[v]++;
            }
        }
        
        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INIT_INDEGREE")
                .description("Calculated in-degrees for all nodes")
                .state(Map.of(
                        "inDegree", Arrays.copyOf(inDegree, inDegree.length)
                ))
                .build());

        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < input.numNodes; i++) {
            if (inDegree[i] == 0) q.add(i);
        }
        
        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INIT_QUEUE")
                .description("Added all nodes with 0 in-degree to the queue")
                .state(Map.of(
                        "inDegree", Arrays.copyOf(inDegree, inDegree.length),
                        "queue", new ArrayList<>(q)
                ))
                .build());

        List<Integer> topoSort = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            topoSort.add(u);
            
            steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("DEQUEUE_NODE")
                    .description("Dequeued node " + u + " and added to topological sort")
                    .state(Map.of(
                            "currentNode", u,
                            "inDegree", Arrays.copyOf(inDegree, inDegree.length),
                            "queue", new ArrayList<>(q),
                            "topoSort", new ArrayList<>(topoSort)
                    ))
                    .build());

            for (int v : input.adjacencyList.getOrDefault(u, new ArrayList<>())) {
                inDegree[v]--;
                
                steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("DECREMENT_INDEGREE")
                        .description(String.format("Removed edge %d -> %d. Decremented in-degree of %d to %d", u, v, v, inDegree[v]))
                        .state(Map.of(
                                "currentNode", u,
                                "activeEdge", List.of(u, v),
                                "inDegree", Arrays.copyOf(inDegree, inDegree.length),
                                "queue", new ArrayList<>(q),
                                "topoSort", new ArrayList<>(topoSort)
                        ))
                        .build());

                if (inDegree[v] == 0) {
                    q.add(v);
                    
                    steps.add(ExecutionStep.builder()
                            .step(stepCount++)
                            .operation("ENQUEUE_NODE")
                            .description(String.format("Node %d in-degree is now 0. Added to queue.", v))
                            .state(Map.of(
                                    "currentNode", u,
                                    "addedNode", v,
                                    "inDegree", Arrays.copyOf(inDegree, inDegree.length),
                                    "queue", new ArrayList<>(q),
                                    "topoSort", new ArrayList<>(topoSort)
                            ))
                            .build());
                }
            }
        }
        
        boolean hasCycle = topoSort.size() != input.numNodes;

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("COMPLETE")
                .description(hasCycle ? "Algorithm terminated. Cycle detected (not all nodes sorted)!" : "Topological Sort (Kahn's) completed.")
                .state(Map.of(
                        "inDegree", Arrays.copyOf(inDegree, inDegree.length),
                        "queue", new ArrayList<>(q),
                        "topoSort", new ArrayList<>(topoSort),
                        "hasCycle", hasCycle
                ))
                .build());

        return steps;
    }

    @Override
    public String getAlgorithmSlug() {
        return "kahn";
    }
}
