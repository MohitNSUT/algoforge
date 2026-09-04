package com.algoforge.algorithm.graph;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DijkstraRunner implements AlgorithmRunner<DijkstraRunner.Input, Map<Integer, Integer>> {

    public static class Input {
        public int numNodes;
        // edges represented as {source: [[dest, weight], ...]}
        public Map<Integer, List<int[]>> adjacencyList;
        public int startNode;
    }

    @Override
    public Map<Integer, Integer> execute(Input input) {
        Map<Integer, Integer> distances = new HashMap<>();
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        Set<Integer> visited = new HashSet<>();

        for (int i = 0; i < input.numNodes; i++) {
            distances.put(i, Integer.MAX_VALUE);
        }

        distances.put(input.startNode, 0);
        pq.offer(new int[]{input.startNode, 0});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int u = current[0];
            int distU = current[1];

            if (visited.contains(u)) continue;
            visited.add(u);

            List<int[]> neighbors = input.adjacencyList.getOrDefault(u, new ArrayList<>());
            for (int[] neighbor : neighbors) {
                int v = neighbor[0];
                int weight = neighbor[1];

                if (!visited.contains(v) && distU + weight < distances.get(v)) {
                    distances.put(v, distU + weight);
                    pq.offer(new int[]{v, distances.get(v)});
                }
            }
        }
        return distances;
    }

    @Override
    public List<ExecutionStep> executeWithSteps(Input input) {
        List<ExecutionStep> steps = new ArrayList<>();
        Map<Integer, Integer> distances = new HashMap<>();
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        Set<Integer> visited = new HashSet<>();
        int stepCount = 1;

        for (int i = 0; i < input.numNodes; i++) {
            distances.put(i, Integer.MAX_VALUE); // Using MAX_VALUE for infinity
        }

        distances.put(input.startNode, 0);
        pq.offer(new int[]{input.startNode, 0});

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INIT")
                .description("Initializing Dijkstra. Start node: " + input.startNode)
                .state(Map.of(
                        "distances", new HashMap<>(distances),
                        "visited", new HashSet<>(visited),
                        "pq", getQueueSnapshot(pq)
                ))
                .build());

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int u = current[0];
            int distU = current[1];

            if (visited.contains(u)) {
                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("SKIP_VISITED")
                    .description("Node " + u + " is already visited. Skipping.")
                    .state(Map.of(
                            "currentNode", u,
                            "distances", new HashMap<>(distances),
                            "visited", new HashSet<>(visited),
                            "pq", getQueueSnapshot(pq)
                    ))
                    .build());
                continue;
            }
            
            visited.add(u);

            steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("VISIT_NODE")
                .description("Visiting node " + u + " with current shortest distance " + distU)
                .state(Map.of(
                        "currentNode", u,
                        "distances", new HashMap<>(distances),
                        "visited", new HashSet<>(visited),
                        "pq", getQueueSnapshot(pq)
                ))
                .build());

            List<int[]> neighbors = input.adjacencyList.getOrDefault(u, new ArrayList<>());
            for (int[] neighbor : neighbors) {
                int v = neighbor[0];
                int weight = neighbor[1];

                steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("CHECK_EDGE")
                    .description(String.format("Checking edge %d -> %d (weight %d)", u, v, weight))
                    .state(Map.of(
                            "currentNode", u,
                            "neighbor", v,
                            "distances", new HashMap<>(distances),
                            "visited", new HashSet<>(visited),
                            "pq", getQueueSnapshot(pq)
                    ))
                    .build());

                if (!visited.contains(v) && distU + weight < distances.get(v)) {
                    distances.put(v, distU + weight);
                    pq.offer(new int[]{v, distances.get(v)});
                    
                    steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("RELAX_EDGE")
                        .description(String.format("Relaxed edge %d -> %d. New distance: %d", u, v, distances.get(v)))
                        .state(Map.of(
                                "currentNode", u,
                                "neighbor", v,
                                "distances", new HashMap<>(distances),
                                "visited", new HashSet<>(visited),
                                "pq", getQueueSnapshot(pq)
                        ))
                        .build());
                }
            }
        }

        steps.add(ExecutionStep.builder()
            .step(stepCount++)
            .operation("COMPLETE")
            .description("Dijkstra's Algorithm completed.")
            .state(Map.of(
                    "distances", new HashMap<>(distances),
                    "visited", new HashSet<>(visited),
                    "pq", getQueueSnapshot(pq)
            ))
            .build());

        return steps;
    }

    private List<Map<String, Integer>> getQueueSnapshot(PriorityQueue<int[]> pq) {
        List<Map<String, Integer>> snapshot = new ArrayList<>();
        for (int[] item : pq) {
            snapshot.add(Map.of("node", item[0], "distance", item[1]));
        }
        return snapshot;
    }

    @Override
    public String getAlgorithmSlug() {
        return "dijkstra";
    }
}
