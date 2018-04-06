package model;

import java.util.ArrayList;

/**
 * Definition is the item specification which is loaded into the PDAMachine instance. It describes
 * the structure of the PDA and holds all the transitions which are possible.
 */
public class Definition {

    private String identifier;
    private ArrayList<ControlState> states;
    private boolean isAcceptByFinalState;
    private ArrayList<Transition> transitions;
    private ControlState initialState;
    private String exampleHint;

    /**
     * An in-depth constructor for a Definition object
     *
     * @param identifier           the id assigned to definition (for saving purposes)
     * @param states               the control states
     * @param initialState         the initial state
     * @param transitions          the transitions which the PDA will be able to make
     * @param isAcceptByFinalState the accepting criterion of the PDA
     */
    public Definition(String identifier, ArrayList<ControlState> states, ControlState initialState, ArrayList<Transition> transitions, boolean isAcceptByFinalState) {
        this.identifier = identifier;
        this.states = states;
        this.isAcceptByFinalState = isAcceptByFinalState;
        this.transitions = transitions;
        this.initialState = initialState;
        initialState.markAsInitial();
    }

    /**
     * A empty Definition constructor
     */
    public Definition() { }

    public void setStates(ArrayList<ControlState> states) {
        this.states = states;
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


    @Override
    public boolean equals(Object obj) {
        return identifier.equals(((Definition) obj).identifier);
    }

    /**
     * A method which adds a transition to the already existing transitions
     * @param newTransition the new transition to add
     */
    public void addTransition(Transition newTransition) {
        transitions.add(newTransition);
    }

    public String getExampleHint() {
        return exampleHint;
    }

    public void setExampleHint(String exampleHint) {
        this.exampleHint = exampleHint;
    }
}
