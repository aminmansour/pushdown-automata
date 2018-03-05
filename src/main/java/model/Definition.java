package model;

import java.util.ArrayList;

public class Definition {

    private String identifier;
    private ArrayList<ControlState> states;
    private boolean isAcceptByFinalState;
    private ArrayList<Transition> transitions;
    private ControlState initialState;

    public Definition(String identifier, ArrayList<ControlState> states, ControlState initialState, ArrayList<Transition> transitions, boolean isAcceptByFinalState) {
        this.identifier = identifier;
//        this.inputAlphabet = inputAlphabet;
        this.states = states;
        this.isAcceptByFinalState = isAcceptByFinalState;
        this.transitions = transitions;
        this.initialState = initialState;
        initialState.markAsInitial();
    }

    public Definition() {
    }

    public void setStates(ArrayList<ControlState> states) {
        this.states = states;
    }

    public static void main(String[] args) {
        ArrayList<Transition> transitions = new ArrayList<>();
        ArrayList<ControlState> states = new ArrayList<>();
        states = new ArrayList<>();
        ControlState q1 = new ControlState("q1");
        states.add(q1);
        ControlState q2 = new ControlState("q2");
        states.add(q2);
        Transition transition1 = new Transition(new Configuration(q1, '1', 'A'), new Action(q1, 'A'));
        transitions.add(transition1);
        Transition transition2 = new Transition(new Configuration(q1, '1', 'A'), new Action(q2, 'A'));
        transitions.add(transition2);
        Transition transition3 = new Transition(new Configuration(q2, '1', 'A'), new Action(q1, 'A'));
        transitions.add(transition3);
        Transition transition4 = new Transition(new Configuration(q1, null, 'A'), new Action(q1, 'A'));
        transitions.add(transition4);
        Definition def = new Definition("1", states, states.get(0), transitions, false);



    }




    public boolean isAcceptByFinalState() {
        return isAcceptByFinalState;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ArrayList<ControlState> getStates() {
        return states;
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }


    public ControlState getInitialState() {
        return initialState;
    }

    public void setInitialState(ControlState initialState) {
        this.initialState = initialState;
    }


    public void setAcceptByFinalState(boolean acceptByFinalState) {
        isAcceptByFinalState = acceptByFinalState;
    }

    public void setTransitions(ArrayList<Transition> transitions) {
        this.transitions = transitions;
    }

    public void prepareForLoad() {
        for (Transition transition : transitions) {
            transition.getConfiguration().setState(retrieveStateByLabel(transition.getConfiguration().getState().getLabel()));
            transition.getAction().setNewState(retrieveStateByLabel(transition.getAction().getNewState().getLabel()));
        }
        initialState = retrieveStateByLabel(initialState.getLabel());
        initialState.markAsInitial();
    }

    private ControlState retrieveStateByLabel(String label) {
        for (ControlState controlState : states) {
            if (controlState.getLabel().equals(label)) {
                return controlState;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return identifier.equals(((Definition) obj).identifier);
    }

    public void addTransition(Transition newTransition) {
        transitions.add(newTransition);
    }
}
