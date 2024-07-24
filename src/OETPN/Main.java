package OETPN;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        PetriExecution executor = new PetriExecution();

        // Initialize inputs
        List<PetriExecution.Transition> transitions = new ArrayList<>();
        transitions.add(executor.new Transition("t1", 3));
        transitions.add(executor.new Transition("t2", 5));
        transitions.add(executor.new Transition("t3", 7));

        int eet = 2;  // Example value for earliest execution time
        int let = 10; // Example value for latest execution time
        String out = "t2"; // Example transition to send out
        String inp = "SampleInput";

        executor.algorithm(transitions, eet, let, out, inp);
    }
}
