package com.algoforge.service;

import com.algoforge.algorithm.core.AlgorithmRunner;
import com.algoforge.algorithm.core.ExecutionStep;
import com.algoforge.algorithm.graph.BfsRunner;
import com.algoforge.algorithm.graph.DfsRunner;
import com.algoforge.algorithm.graph.DijkstraRunner;
import com.algoforge.algorithm.graph.BellmanFordRunner;
import com.algoforge.algorithm.graph.FloydWarshallRunner;
import com.algoforge.algorithm.graph.AStarRunner;
import com.algoforge.algorithm.graph.UnionFindRunner;
import com.algoforge.algorithm.graph.KruskalRunner;
import com.algoforge.algorithm.graph.PrimRunner;
import com.algoforge.algorithm.graph.KahnRunner;
import com.algoforge.algorithm.graph.DfsTopoRunner;
import com.algoforge.algorithm.searching.BinarySearchRunner;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AlgorithmExecutionService {

    private final Map<String, AlgorithmRunner<?, ?>> runners;
    private final ObjectMapper objectMapper;

    public AlgorithmExecutionService(List<AlgorithmRunner<?, ?>> runnerList) {
        this.runners = runnerList.stream()
                .collect(Collectors.toMap(AlgorithmRunner::getAlgorithmSlug, Function.identity()));
        this.objectMapper = new ObjectMapper();
    }

    @SuppressWarnings("unchecked")
    public List<ExecutionStep> executeAlgorithmSteps(String slug, Map<String, Object> payload) {
        AlgorithmRunner<?, ?> runner = runners.get(slug);

        if (runner == null) {
            throw new IllegalArgumentException("Algorithm not found for slug: " + slug);
        }

        Object input = parsePayloadForRunner(runner, payload);

        // Perform cast to raw type to call executeWithSteps
        return ((AlgorithmRunner<Object, ?>) runner).executeWithSteps(input);
    }

    private Object parsePayloadForRunner(AlgorithmRunner<?, ?> runner, Map<String, Object> payload) {
        String slug = runner.getAlgorithmSlug();
        if (slug.equals("bubble-sort") || slug.equals("merge-sort") || slug.equals("quick-sort")
                || slug.equals("insertion-sort") || slug.equals("selection-sort")) {
            List<Integer> arrayList = objectMapper.convertValue(payload.get("array"),
                    new TypeReference<List<Integer>>() {
                    });
            if (arrayList == null)
                throw new IllegalArgumentException("Missing 'array' in payload");
            return arrayList.stream().mapToInt(i -> i).toArray();
        } else if (slug.equals("binary-search")) {
            BinarySearchRunner.Input input = new BinarySearchRunner.Input();
            List<Integer> arrayList = objectMapper.convertValue(payload.get("array"),
                    new TypeReference<List<Integer>>() {
                    });
            if (arrayList == null)
                throw new IllegalArgumentException("Missing 'array' in payload");
            input.array = arrayList.stream().mapToInt(i -> i).toArray();
            input.target = (Integer) payload.get("target");
            return input;
        } else if (slug.equals("dijkstra")) {
            DijkstraRunner.Input input = new DijkstraRunner.Input();
            input.numNodes = (Integer) payload.get("numNodes");
            input.startNode = (Integer) payload.get("startNode");
            input.adjacencyList = objectMapper.convertValue(payload.get("adjacencyList"),
                    new TypeReference<Map<Integer, List<int[]>>>() {
                    });
            return input;
        } else if (slug.equals("prim")) {
            PrimRunner.Input input = new PrimRunner.Input();
            input.numNodes = (Integer) payload.get("numNodes");
            input.startNode = (Integer) payload.get("startNode");
            input.adjacencyList = objectMapper.convertValue(payload.get("adjacencyList"),
                    new TypeReference<Map<Integer, List<int[]>>>() {
                    });
            return input;
        } else if (slug.equals("a-star")) {
            AStarRunner.Input input = new AStarRunner.Input();
            input.numNodes = (Integer) payload.get("numNodes");
            input.startNode = (Integer) payload.get("startNode");
            input.targetNode = (Integer) payload.get("targetNode");
            input.adjacencyList = objectMapper.convertValue(payload.get("adjacencyList"),
                    new TypeReference<Map<Integer, List<int[]>>>() {
                    });
            input.coordinates = objectMapper.convertValue(payload.get("coordinates"),
                    new TypeReference<Map<Integer, int[]>>() {
                    });
            return input;
        } else if (slug.equals("bellman-ford")) {
            BellmanFordRunner.Input input = new BellmanFordRunner.Input();
            input.numNodes = (Integer) payload.get("numNodes");
            input.startNode = (Integer) payload.get("startNode");
            input.edges = objectMapper.convertValue(payload.get("edges"), new TypeReference<List<int[]>>() {
            });
            return input;
        } else if (slug.equals("kruskal")) {
            KruskalRunner.Input input = new KruskalRunner.Input();
            input.numNodes = (Integer) payload.get("numNodes");
            input.edges = objectMapper.convertValue(payload.get("edges"), new TypeReference<List<int[]>>() {
            });
            return input;
        } else if (slug.equals("floyd-warshall")) {
            FloydWarshallRunner.Input input = new FloydWarshallRunner.Input();
            input.numNodes = (Integer) payload.get("numNodes");
            input.graph = objectMapper.convertValue(payload.get("graph"), int[][].class);
            return input;
        } else if (slug.equals("union-find")) {
            UnionFindRunner.Input input = new UnionFindRunner.Input();
            input.numNodes = (Integer) payload.get("numNodes");
            input.operations = objectMapper.convertValue(payload.get("operations"), new TypeReference<List<int[]>>() {
            });
            return input;
        } else if (slug.equals("bfs")) {
            BfsRunner.Input input = new BfsRunner.Input();
            input.numNodes = (Integer) payload.get("numNodes");
            input.startNode = (Integer) payload.get("startNode");
            input.adjacencyList = objectMapper.convertValue(payload.get("adjacencyList"),
                    new TypeReference<Map<Integer, List<Integer>>>() {
                    });
            return input;
        } else if (slug.equals("dfs")) {
            DfsRunner.Input input = new DfsRunner.Input();
            input.numNodes = (Integer) payload.get("numNodes");
            input.startNode = (Integer) payload.get("startNode");
            input.adjacencyList = objectMapper.convertValue(payload.get("adjacencyList"),
                    new TypeReference<Map<Integer, List<Integer>>>() {
                    });
            return input;
        } else if (slug.equals("kahn")) {
            KahnRunner.Input input = new KahnRunner.Input();
            input.numNodes = (Integer) payload.get("numNodes");
            input.adjacencyList = objectMapper.convertValue(payload.get("adjacencyList"),
                    new TypeReference<Map<Integer, List<Integer>>>() {
                    });
            return input;
        } else if (slug.equals("dfs-topo")) {
            DfsTopoRunner.Input input = new DfsTopoRunner.Input();
            input.numNodes = (Integer) payload.get("numNodes");
            input.adjacencyList = objectMapper.convertValue(payload.get("adjacencyList"),
                    new TypeReference<Map<Integer, List<Integer>>>() {
                    });
            return input;
        }

        throw new IllegalArgumentException("Unsupported payload parsing for algorithm: " + slug);
    }
}
