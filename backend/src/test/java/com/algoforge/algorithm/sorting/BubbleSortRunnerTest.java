package com.algoforge.algorithm.sorting;

import com.algoforge.algorithm.core.ExecutionStep;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BubbleSortRunnerTest {

    private final BubbleSortRunner runner = new BubbleSortRunner();

    @Test
    void execute_sortsArrayCorrectly() {
        int[] input = {5, 3, 8, 4, 2};
        int[] result = runner.execute(input);
        assertArrayEquals(new int[]{2, 3, 4, 5, 8}, result);
    }

    @Test
    void execute_alreadySorted() {
        int[] input = {1, 2, 3, 4, 5};
        int[] result = runner.execute(input);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, result);
    }

    @Test
    void executeWithSteps_generatesSteps() {
        int[] input = {2, 1};
        List<ExecutionStep> steps = runner.executeWithSteps(input);
        
        assertFalse(steps.isEmpty());
        assertEquals("INIT", steps.get(0).getOperation());
        assertEquals("COMPLETE", steps.get(steps.size() - 1).getOperation());
        
        // 2, 1 should have INIT, COMPARE, SWAP, COMPLETE
        assertTrue(steps.stream().anyMatch(s -> s.getOperation().equals("SWAP")));
    }
}
