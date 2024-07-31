package OETPN;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        PetriExecution petriExecution = new PetriExecution();

        PetriExecution.Transition T1 = petriExecution.new Transition("T1", 3);
        PetriExecution.Transition T2 = petriExecution.new Transition("T2", 2);

        // Pre and post matrices
        int[][] preMatrix = {
                {1, 0}, // T1 takes one token from P1
                {0, 1}, // T2 takes one token from P2
                {0, 0}  // P3 is not an input for any transition
        };

        int[][] postMatrix = {
                {0, 0}, // P1 - not an output for any transition
                {1, 0}, // T1 puts one token in P2
                {0, 1}  // T2 puts one token in P3
        };

        petriExecution.algorithm(Arrays.asList(T1, T2), preMatrix, postMatrix, 1, 5, "T2", "AddTokenP2");
    }
}
