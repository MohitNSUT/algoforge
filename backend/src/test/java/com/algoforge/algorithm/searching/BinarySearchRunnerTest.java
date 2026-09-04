package com.algoforge.algorithm.searching;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BinarySearchRunnerTest {

    private final BinarySearchRunner runner = new BinarySearchRunner();

    @Test
    void execute_findsTarget() {
        BinarySearchRunner.Input input = new BinarySearchRunner.Input();
        input.array = new int[]{1, 3, 5, 7, 9};
        input.target = 7;
        
        assertEquals(3, runner.execute(input));
    }

    @Test
    void execute_targetNotFound() {
        BinarySearchRunner.Input input = new BinarySearchRunner.Input();
        input.array = new int[]{1, 3, 5, 7, 9};
        input.target = 4;
        
        assertEquals(-1, runner.execute(input));
    }
}
