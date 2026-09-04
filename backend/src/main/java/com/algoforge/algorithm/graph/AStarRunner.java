package com.algoforge.algorithm.graph;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AStarRunner implements AlgorithmRunner<AStarRunner.Input, List<Integer>> {

    public static class Input {
        public int numNodes;
        public int startNode;
        public int targetNode;
        // Edge format: adjacencyList.get(node) -> [neighbor, weight]
        public Map<Integer, List<int[]>> adjacencyList;
        // Coordinates for heuristic calculation: map node -> [x, y]
        public Map<Integer, int[]> coordinates;
    }

    private double heuristic(int node, int target, Map<Integer, int[]> coords) {
        if (coords == null || !coords.containsKey(node) || !coords.containsKey(target)) return 0;
        int[] p1 = coords.get(node);
        int[] p2 = coords.get(target);
        return Math.sqrt(Math.pow(p1[0] - p2[0], 2) + Math.pow(p1[1] - p2[1], 2));
    }

    @Override
    public List<Integer> execute(Input input) {
        PriorityQueue<double[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));
        double[] gScore = new double[input.numNodes];
        Arrays.fill(gScore, Double.MAX_VALUE);
        int[] parent = new int[input.numNodes];
        Arrays.fill(parent, -1);

        gScore[input.startNode] = 0;
        pq.offer(new double[]{input.startNode, heuristic(input.startNode, input.targetNode, input.coordinates)});

        while (!pq.isEmpty()) {
            double[] curr = pq.poll();
            int u = (int) curr[0];

            if (u == input.targetNode) {
                return reconstructPath(parent, u);
            }

            for (int[] edge : input.adjacencyList.getOrDefault(u, new ArrayList<>())) {
                int v = edge[0];
                int weight = edge[1];
                double tentativeGScore = gScore[u] + weight;

                if (tentativeGScore < gScore[v]) {
                    parent[v] = u;
                    gScore[v] = tentativeGScore;
                    double fScore = gScore[v] + heuristic(v, input.targetNode, input.coordinates);
                    pq.offer(new double[]{v, fScore});
                }
            }
        }
        return new ArrayList<>(); // Path not found
    }

    private List<Integer> reconstructPath(int[] parent, int current) {
        List<Integer> path = new ArrayList<>();
        while (current != -1) {
            path.add(current);
            current = parent[current];
        }
        Collections.reverse(path);
        return path;
    }

    @Override
    public List<ExecutionStep> executeWithSteps(Input input) {
        List<ExecutionStep> steps = new ArrayList<>();
        int stepCount = 1;

        PriorityQueue<double[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));
        double[] gScore = new double[input.numNodes];
        Arrays.fill(gScore, Double.MAX_VALUE);
        int[] parent = new int[input.numNodes];
        Arrays.fill(parent, -1);

        gScore[input.startNode] = 0;
        double startFScore = heuristic(input.startNode, input.targetNode, input.coordinates);
        pq.offer(new double[]{input.startNode, startFScore});

        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("INIT")
                .description(String.format("Initialized A*. Start Node: %d, Target: %d. Initial F-Score: %.2f", input.startNode, input.targetNode, startFScore))
                .state(Map.of(
                        "gScores", copyScores(gScore),
                        "parents", Arrays.copyOf(parent, parent.length),
                        "queue", copyQueue(pq)
                ))
                .build());

        Set<Integer> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            double[] curr = pq.poll();
            int u = (int) curr[0];
            visited.add(u);

            steps.add(ExecutionStep.builder()
                    .step(stepCount++)
                    .operation("EVALUATE_NODE")
                    .description("Evaluating node " + u + " with F-score " + curr[1])
                    .state(Map.of(
                            "currentNode", u,
                            "gScores", copyScores(gScore),
                            "parents", Arrays.copyOf(parent, parent.length),
                            "visited", new HashSet<>(visited),
                            "queue", copyQueue(pq)
                    ))
                    .build());

            if (u == input.targetNode) {
                List<Integer> path = reconstructPath(parent, u);
                steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("TARGET_FOUND")
                        .description("Target reached! Final path: " + path)
                        .state(Map.of(
                                "currentNode", u,
                                "path", path,
                                "gScores", copyScores(gScore),
                                "parents", Arrays.copyOf(parent, parent.length),
                                "visited", new HashSet<>(visited)
                        ))
                        .build());
                return steps;
            }

            for (int[] edge : input.adjacencyList.getOrDefault(u, new ArrayList<>())) {
                int v = edge[0];
                int weight = edge[1];
                double tentativeGScore = gScore[u] + weight;
                
                steps.add(ExecutionStep.builder()
                        .step(stepCount++)
                        .operation("CHECK_NEIGHBOR")
                        .description(String.format("Checking neighbor %d. Tentative G-Score: %.2f", v, tentativeGScore))
                        .state(Map.of(
                                "currentNode", u,
                                "neighbor", v,
                                "gScores", copyScores(gScore),
                                "parents", Arrays.copyOf(parent, parent.length),
                                "visited", new HashSet<>(visited),
                                "queue", copyQueue(pq)
                        ))
                        .build());

                if (tentativeGScore < gScore[v]) {
                    parent[v] = u;
                    gScore[v] = tentativeGScore;
                    double hScore = heuristic(v, input.targetNode, input.coordinates);
                    double fScore = gScore[v] + hScore;
                    pq.offer(new double[]{v, fScore});
                    
                    steps.add(ExecutionStep.builder()
                            .step(stepCount++)
                            .operation("UPDATE_SCORE")
                            .description(String.format("Updated node %d. New G-Score: %.2f, F-Score: %.2f (Heuristic: %.2f)", v, gScore[v], fScore, hScore))
                            .state(Map.of(
                                    "currentNode", u,
                                    "neighbor", v,
                                    "gScores", copyScores(gScore),
                                    "parents", Arrays.copyOf(parent, parent.length),
                                    "visited", new HashSet<>(visited),
                                    "queue", copyQueue(pq)
                            ))
                            .build());
                }
            }
        }
        
        steps.add(ExecutionStep.builder()
                .step(stepCount++)
                .operation("COMPLETE_NO_PATH")
                .description("Queue is empty and target was not reached.")
                .state(Map.of(
                        "gScores", copyScores(gScore),
                        "parents", Arrays.copyOf(parent, parent.length),
                        "visited", new HashSet<>(visited)
                ))
                .build());

        return steps;
    }

    private double[] copyScores(double[] scores) {
        double[] copy = new double[scores.length];
        for(int i = 0; i < scores.length; i++) {
            copy[i] = scores[i] == Double.MAX_VALUE ? -1 : scores[i];
        }
        return copy;
    }

    private List<Map<String, Double>> copyQueue(PriorityQueue<double[]> pq) {
        List<Map<String, Double>> list = new ArrayList<>();
        for (double[] elem : pq) {
            list.add(Map.of("node", elem[0], "fScore", elem[1]));
        }
        return list;
    }

    @Override
    public String getAlgorithmSlug() {
        return "a-star";
    }
}
