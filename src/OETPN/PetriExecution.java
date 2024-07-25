package OETPN;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PetriExecution {

    //events, states, transitions
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
        //initial token count
        int P1 = 1;
        int P2 = 0;
        int P3 = 0;

        @Override
        public String toString() {
            return "State{P1=" + P1 + ", P2=" + P2 + ", P3=" + P3 + "}";
        }
    }

    List<Transition> execList = new ArrayList<>();
    State M;  // initial marking
    int timeHorizon = 7;
    int currentTime = 0; //counter

    Queue<Event> eventQueue = new LinkedList<>();
    Queue<String> inputQueue = new LinkedList<>();

    Event waitEvent() {

        while (eventQueue.isEmpty()) {
            if (currentTime >= timeHorizon) {
                return null; // no more events and time horizon reached
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Waiting for event");
            currentTime++; //for each wait, increment
        }
        Event event = eventQueue.poll();
        System.out.println("The received event: " + event.type);
        return event;
    }

    void receive(String inp) {
        inputQueue.add(inp);
        System.out.println("The received input: " + inp);
    }

    void updateState() {
        if (!inputQueue.isEmpty()) {
            String input = inputQueue.poll();

            if (input.equals("AddTokenP1")) {
                M.P1++;
            } else if (input.equals("RemoveTokenP1")) {
                if (M.P1 > 0) M.P1--;
            } else if (input.equals("AddTokenP2")) {
                M.P2++;
            } else if (input.equals("RemoveTokenP2")) {
                if (M.P2 > 0) M.P2--;
            } else if (input.equals("AddTokenP3")) {
                M.P3++;
            } else if (input.equals("RemoveTokenP3")) {
                if (M.P3 > 0) M.P3--;
            }
            System.out.println("State updated based on the input: " + input + " - " + M);
        }
    }

    boolean conditionForTransition(Transition t) {

        if (t.id.equals("T1")) {
            return M.P1 > 0;
        } else if (t.id.equals("T2")) {
            return M.P2 > 0;
        }
        return false;
    }

    void moveTokens(Transition t) {
        if (t.id.equals("T1")) {
            M.P1--;
            M.P2++;
        } else if (t.id.equals("T2")) {
            M.P2--;
            M.P3++;
        }
        System.out.println("Moved tokens for the transition: " + t.id + " -> " + M);
    }

    void send(String out) {
        System.out.println("Output: " + out);
        try (FileWriter writer = new FileWriter("output.txt", true)) {
            writer.write("Output: " + out + "\n");
            System.out.println("Output: " + out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void algorithm(List<Transition> transitions, int eet, int let, String out, String inp) {
        boolean moreTransitions;

        M = new State();  //m is the initial state
        execList = new ArrayList<>();
        System.out.println("Initialization complete. Initial state is: " + M);

        // add initial events and input
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
                currentTime++; // increment for each tic event
            } else if ("input".equals(event.type)) {
                receive(inp);
                updateState();
            }

            do {
                moreTransitions = false;

                for (Transition t : transitions) {
                    if (conditionForTransition(t) && !execList.contains(t)) {
                        moveTokens(t);
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
                        moveTokens(t);
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
