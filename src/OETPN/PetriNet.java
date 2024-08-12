package OETPN;

import java.util.*;

public class PetriNet {
    int[][] pre;
    int[][] post;
    int[] marking;
    int[] delay;

    HashMap<Integer, Integer> inExecution; // key=transition index, value=remaining delay
    private int nrTransitions;

    public PetriNet(int[][] pre, int[][] post, int[] marking, int[] delay) {
        this.pre = pre;
        this.post = post;
        this.marking = marking;
        this.delay = delay;
        this.inExecution = new HashMap<>();
        this.nrTransitions = post[0].length;
    }

    public void addTokens(int[] additionalMarking){
        for (int i=0;i<additionalMarking.length;i++){
            marking[i] = marking[i] + additionalMarking[i];
        }
    }

    public void step(String eventName) {
        boolean somethingWasExecuted = false;
        do {
            //build a list of executable transitions
            List<Integer> executables = new ArrayList<>();
            for (int t = 0; t < nrTransitions; t++) {
                if (isExecutableTransition(t)) {
                    executables.add(t);
                }
            }

            if (executables.size() > 0) {
                somethingWasExecuted = true;

                int transitionIndexToRun = executables.get(new Random().nextInt(executables.size()));
                startTransition(transitionIndexToRun);
                if (delay[transitionIndexToRun] > 0) {
                    inExecution.put(transitionIndexToRun, delay[transitionIndexToRun]);
                } else {
                    finalizeTransition(transitionIndexToRun);
                }
            }
            else{
                somethingWasExecuted = false;
            }
        }
        while (somethingWasExecuted);

        if ("tic".equals(eventName)) {
            List<Integer> toDelete = new ArrayList<>();
            for (int transitionIndex : inExecution.keySet()) {
                inExecution.put(transitionIndex, inExecution.get(transitionIndex) - 1);
                if (inExecution.get(transitionIndex) == 0) {
                    finalizeTransition(transitionIndex);
                    toDelete.add(transitionIndex);
                }
            }
            for (int t : toDelete){
                inExecution.remove(t);
            }
        }
    }

    boolean isExecutableTransition(int transitionIndex) {
        for (int p = 0; p < pre.length; p++) {
            if (marking[p] < pre[p][transitionIndex]) {
                return false;
            }
        }
        return true;
    }

    void startTransition(int transitionIndex) {
        for (int i = 0; i < pre.length; i++) {
            marking[i] -= pre[i][transitionIndex];
        }
    }

    void finalizeTransition(int transitionIndex) {
        for (int i = 0; i < pre.length; i++) {
            marking[i] += post[i][transitionIndex];
        }
    }

    public String toString(){
        String s = "";
        s = s + "Marking: ";
        for (int i=0;i<marking.length;i++){
            s = s + marking[i] + " ";
        }

        s = s + "In execution: "; // T0: remaining: 4,
        for (int t : inExecution.keySet()){
            s = s + "T" + t + ": remaining: " + inExecution.get(t) + ", ";
        }

        return s;
    }
}
