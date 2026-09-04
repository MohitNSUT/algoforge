package com.algoforge.algorithm.graph;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraRunnerTest {

    private final DijkstraRunner runner = new DijkstraRunner();

    @Test
    void execute_findsShortestPaths() {
        DijkstraRunner.Input input = new DijkstraRunner.Input();
        input.numNodes = 4;
        input.startNode = 0;
        input.adjacencyList = Map.of(
            0, List.of(new int[]{1, 1}, new int[]{2, 4}),
            1, List.of(new int[]{2, 2}, new int[]{3, 6}),
            2, List.of(new int[]{3, 3}),
            3, List.of()
        );

        Map<Integer, Integer> result = runner.execute(input);
        
        assertEquals(0, result.get(0));
        assertEquals(1, result.get(1));
        assertEquals(3, result.get(2)); // 0 -> 1 -> 2 (1 + 2 = 3)
        assertEquals(6, result.get(3)); // 0 -> 1 -> 2 -> 3 (1 + 2 + 3 = 6)
    }
}
