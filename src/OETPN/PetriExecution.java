package OETPN;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PetriExecution {

    public class Event {
        String type;

        public Event(String type) {
            this.type = type;
        }
    }

    public class Transition {
        String id;
        int delay;

        public Transition(String id, int delay) {
            this.id = id;
            this.delay = delay;
        }

        @Override
        public String toString() {
            return "Transition{id='" + id + "', delay=" + delay + "}";
        }
    }

    public class State {
        int[] tokens;

        public State(int numPlaces) {
            tokens = new int[numPlaces];
        }

        @Override
        public String toString() {
            return "State" + Arrays.toString(tokens);
        }
    }

    List<Transition> execList = new ArrayList<>();
    State M;
    int timeHorizon = 10;
    int currentTime = 0;

    Queue<Event> eventQueue = new LinkedList<>();
    Queue<String> inputQueue = new LinkedList<>();
    int[][] preMatrix;
    int[][] postMatrix;
    List<String> outputs = new ArrayList<>();

    Event waitEvent() {
        while (eventQueue.isEmpty()) {
            if (currentTime >= timeHorizon) {
                return null;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Waiting for event");
            currentTime++;
        }
        Event event = eventQueue.poll();
        System.out.println("Received event: " + event.type);
        return event;
    }

    void receive(String inp) {
        inputQueue.add(inp);
        System.out.println("Received input: " + inp);
    }

    void updateState() {
        if (!inputQueue.isEmpty()) {
            String input = inputQueue.poll();
            // Example logic to update state based on input
            // Adjust this to match the logic of your Petri net
            System.out.println("State updated based on input: " + input + " -> " + M);
        }
    }

    boolean conditionForTransition(Transition t, int index) {
        for (int i = 0; i < preMatrix.length; i++) {
            if (M.tokens[i] < preMatrix[i][index]) {
                return false;
            }
        }
        return true;
    }

    void moveTokens(Transition t, int index) {
        for (int i = 0; i < preMatrix.length; i++) {
            M.tokens[i] -= preMatrix[i][index];
            M.tokens[i] += postMatrix[i][index];
        }
        System.out.println("Moved tokens for transition: " + t.id + " -> " + M);
    }

    void send(String out) {
        outputs.add(out);
        System.out.println("Output: " + out);
        try (FileWriter writer = new FileWriter("output.txt", true)) {
            writer.write("Output: " + out + "\n");
            System.out.println("Output: " + out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void algorithm(List<Transition> transitions, int[][] preMatrix, int[][] postMatrix, int eet, int let, String out, String inp) {
        this.preMatrix = preMatrix;
        this.postMatrix = postMatrix;

        M = new State(preMatrix.length);
        M.tokens[0] = 1; // Set initial state with 1 token in P1
        execList = new ArrayList<>();
        System.out.println("Initialization complete. Initial state: " + M);

        eventQueue.add(new Event("tic"));
        eventQueue.add(new Event("tic"));
        eventQueue.add(new Event("tic"));
        eventQueue.add(new Event("input"));
        inputQueue.add("AddTokenP1");
        eventQueue.add(new Event("tic"));
        eventQueue.add(new Event("tic"));

        while (currentTime < timeHorizon) {
            Event event = waitEvent();
            if (event == null) {
                System.out.println("No more events. Stopping algorithm.");
                break;
            }

            if ("tic".equals(event.type)) {
                System.out.println("Decreasing delays for transitions in execList");
                for (Transition t : execList) {
                    t.delay--;
                }
                currentTime++;
            } else if ("input".equals(event.type)) {
                receive(inp);
                updateState();
            }

            boolean moreTransitions;
            do {
                moreTransitions = false;

                for (int i = 0; i < transitions.size(); i++) {
                    Transition t = transitions.get(i);
                    if (conditionForTransition(t, i) && !execList.contains(t)) {
                        moveTokens(t, i);
                        execList.add(t);
                        t.delay = getDelay(t, eet, let);
                        System.out.println("Transition " + t.id + " added to execList with delay " + t.delay);
                    }
                }

                Iterator<Transition> iterator = execList.iterator();
                while (iterator.hasNext()) {
                    Transition t = iterator.next();
                    if (t.delay == 0) {
                        iterator.remove();
                        int index = transitions.indexOf(t);
                        moveTokens(t, index);
                        System.out.println("Transition " + t.id + " executed.");
                        if (isOutTransition(t, out)) {
                            send(out);
                        }
                        moreTransitions = true;
                    }
                }
            } while (moreTransitions);

            System.out.println("End of outer while loop iteration. Current time: " + currentTime + ", Time horizon: " + timeHorizon);
        }

        System.out.println("Algorithm completed.");
    }

    int getDelay(Transition t, int eet, int let) {
        return t.delay > let ? let : t.delay < eet ? eet : t.delay;
    }

    boolean isOutTransition(Transition t, String out) {
        return t.id.equals(out);
    }
}
