package OETPN;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        PetriExecution petriExecution = new PetriExecution();

        // Example transitions
        PetriExecution.Transition T1 = petriExecution.new Transition("T1", 3);
        PetriExecution.Transition T2 = petriExecution.new Transition("T2", 2);

        // Running the algorithm
        petriExecution.algorithm(Arrays.asList(T1, T2), 1, 5, "T2", "AddTokenP2");
    }
}
