package com.algoforge.algorithm.graph;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PrimRunner implements AlgorithmRunner<PrimRunner.Input, List<int[]>> {

    public static class Input {
        public int numNodes;
        public int startNode;
        // Edge format: adjacencyList.get(node) -> [neighbor, weight]
        public Map<Integer, List<int[]>> adjacencyList;
    }

    @Override
    public List<int[]> execute(Input input) {
        List<int[]> mst = new ArrayList<>();
        boolean[] inMST = new boolean[input.numNodes];
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        
        inMST[input.startNode] = true;
        for (int[] edge : input.adjacencyList.getOrDefault(input.startNode, new ArrayList<>())) {
            pq.offer(new int[]{input.startNode, edge[0], edge[1]});
        }

        while (!pq.isEmpty() && mst.size() < input.numNodes - 1) {
            int[] edge = pq.poll();
            int v = edge[1];

            if (inMST[v]) continue;

            inMST[v] = true;
            mst.add(edge);

            for (int[] nextEdge : input.adjacencyList.getOrDefault(v, new ArrayList<>())) {
                if (!inMST[nextEdge[0]]) {
                    pq.offer(new int[]{v, nextEdge[0], nextEdge[1]});
                }
            }
        }
        return mst;
    }

    @Override
    public List<ExecutionStep> executeWithSteps(Input input) {
        List<ExecutionStep> steps = new ArrayList<>();
        List<int[]> mst = new ArrayList<>();
        int stepCount = 1;
        
        boolean[] inMST = new boolean[input.numNodes];
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));

        inMST[input.startNode] = true;
        for (int[] edge : input.adjacencyList.getOrDefault(input.startNode, new ArrayList<>())) {
            pq.offer(new int[]{input.startNode, edge[0], edge[1]});
        }
        
        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INIT")
                .description("Initializing Prim's Algorithm from node " + input.startNode)
                .state(Map.of(
                        "mst", new ArrayList<>(mst),
                        "inMST", copyInMST(inMST),
                        "queue", copyQueue(pq)
                ))
                .build());

        while (!pq.isEmpty() && mst.size() < input.numNodes - 1) {
            int[] edge = pq.poll();
            int u = edge[0];
            int v = edge[1];
            int weight = edge[2];
            
            steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("EVALUATE_EDGE")
                    .description(String.format("Evaluating smallest edge in cut: %d -> %d (weight %d)", u, v, weight))
                    .state(Map.of(
                            "mst", new ArrayList<>(mst),
                            "activeEdge", edge,
                            "inMST", copyInMST(inMST),
                            "queue", copyQueue(pq)
                    ))
                    .build());

            if (inMST[v]) {
                steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("SKIP_EDGE")
                        .description(String.format("Node %d is already in MST. Skipping edge.", v))
                        .state(Map.of(
                                "mst", new ArrayList<>(mst),
                                "discardedEdge", edge,
                                "inMST", copyInMST(inMST),
                                "queue", copyQueue(pq)
                        ))
                        .build());
                continue;
            }

            inMST[v] = true;
            mst.add(edge);
            
            steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("EDGE_ADDED")
                    .description(String.format("Added node %d to MST via edge %d -> %d.", v, u, v))
                    .state(Map.of(
                            "mst", new ArrayList<>(mst),
                            "addedEdge", edge,
                            "inMST", copyInMST(inMST),
                            "queue", copyQueue(pq)
                    ))
                    .build());

            for (int[] nextEdge : input.adjacencyList.getOrDefault(v, new ArrayList<>())) {
                if (!inMST[nextEdge[0]]) {
                    int[] newQEdge = new int[]{v, nextEdge[0], nextEdge[1]};
                    pq.offer(newQEdge);
                    
                    steps.add(ExecutionStep.builder()
                            .step(stepCount++)
                            .operation("ENQUEUE_EDGE")
                            .description(String.format("Adding edge %d -> %d to Priority Queue", v, nextEdge[0]))
                            .state(Map.of(
                                    "mst", new ArrayList<>(mst),
                                    "inMST", copyInMST(inMST),
                                    "queue", copyQueue(pq)
                            ))
                            .build());
                }
            }
        }
        
        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("COMPLETE")
                .description("Prim's Algorithm completed.")
                .state(Map.of(
                        "mst", new ArrayList<>(mst),
                        "inMST", copyInMST(inMST)
                ))
                .build());

        return steps;
    }
    
    private List<Integer> copyInMST(boolean[] inMST) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < inMST.length; i++) {
            if (inMST[i]) list.add(i);
        }
        return list;
    }
    
    private List<Map<String, Integer>> copyQueue(PriorityQueue<int[]> pq) {
        List<Map<String, Integer>> list = new ArrayList<>();
        for (int[] edge : pq) {
            list.add(Map.of("u", edge[0], "v", edge[1], "weight", edge[2]));
        }
        return list;
    }

    @Override
    public String getAlgorithmSlug() {
        return "prim";
    }
}
