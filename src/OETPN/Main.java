package OETPN;

import java.util.Arrays;

public class Main {
    //    public static void main(String[] args) {
//        PetriExecution petriExecution = new PetriExecution();
//
//        PetriExecution.Transition T1 = petriExecution.new Transition("T1", 3);
//        PetriExecution.Transition T2 = petriExecution.new Transition("T2", 2);
//
//        // Pre and post matrices
//        int[][] preMatrix = {
//                {1, 0}, // T1 takes one token from P1
//                {0, 1}, // T2 takes one token from P2
//                {0, 0}  // P3 is not an input for any transition
//        };
//
//        int[][] postMatrix = {
//                {0, 0}, // P1 - not an output for any transition
//                {1, 0}, // T1 puts one token in P2
//                {0, 1}  // T2 puts one token in P3
//        };
//
//        petriExecution.algorithm(Arrays.asList(T1, T2), preMatrix, postMatrix, 1, 5, "T2", "AddTokenP2");
//    }
    public static void Example1(){
        // Pre and post matrices () -> |3 -> () -> |2 -> ()
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
        int[] delays = {3, 2};
        int[] initMarking = {1, 0, 0};

        PetriNet pn = new PetriNet(preMatrix, postMatrix, initMarking, delays);
        System.out.println("INITIAL: " + pn.toString());
        for (int i=0;i<10;i++){
            pn.step("tic");
            System.out.println(pn.toString());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
        }
    }

    public static void Example2(){
        // Pre and post matrices (P0) -> |0 -> (P1) -> |2 -> (P3)
        //                               |  -> (P2) -> |3 -> (P4)
        int[][] preMatrix = {
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1},
                {0, 0, 0},
                {0, 0, 0},
        };

        int[][] postMatrix = {
                {0, 0, 0},
                {1, 0, 0},
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1},
        };
        int[] delays = {0, 2, 3};
        int[] initMarking = {1, 0, 0, 0, 0};

        PetriNet pn = new PetriNet(preMatrix, postMatrix, initMarking, delays);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        pn.addTokens(new int[] {1, 0, 0, 0, 0});
                        pn.step("input");
                        System.out.println("input" + ": " + pn.toString());
                    }
                },
                2500
        );

        System.out.println("INITIAL: " + pn.toString());
        for (int i=0;i<10;i++){
            pn.step("tic");
            System.out.println("step " + i + ": " + pn.toString());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
        }
        System.out.println("Rularea s-a terminat");
    }


    public static void main(String[] args) throws InterruptedException {
        Example2();
    }
}
