package model;

import java.util.ArrayList;

public class PDAMachine {
    private boolean gameOver;
    private Definition loadedDefinition;
    private InputTape tape;
    private PushDownStack stack;
    private ControlState currentState;
    private ComputationalTree history;

    public PDAMachine(Definition defToLoad) {
        loadDefinition(defToLoad);
    }

    private void loadDefinition(Definition definition){
        gameOver = false;
        loadedDefinition = definition;
        currentState = loadedDefinition.getInitialState();
        tape = new InputTape();
        stack = new PushDownStack();
        tape.clear();
        stack.clear();
    }

    private void executeTransition(Transition transition,int totalChildren) {
        tape.readSymbol();
        if(transition.getConfiguration().getTopElement()!=null){
            stack.pop();
        }
        Character elementToPush = transition.getAction().getElementToPush();
        if(elementToPush !=null){
            stack.push(elementToPush);
        }
        currentState = transition.getAction().getNewState();
        Configuration config = new Configuration(currentState, tape.getSymbolAtHead(), stack.top());

    }


    public ArrayList<Transition> getPossibleTransitionsFromCurrent() {
        return loadedDefinition.getPossibleTransitions(currentState, tape.getSymbolAtHead(), stack.top());
    }

    public InputTape getTape() {
        return tape;
    }

    public PushDownStack getStack() {
        return stack;
    }

    public Definition getDefinition() {
        return loadedDefinition;
    }


    public void executeTransition(Transition transition, boolean isBranchingTransition) {
        tape.readSymbol();
        if (transition.getConfiguration().getTopElement() != '/') {
            stack.pop();
        }
        if (transition.getAction().getElementToPush() != '/') {
            stack.push(transition.getAction().getElementToPush());
        }
        currentState = transition.getAction().getNewState();
        ConfigurationNode child = new ConfigurationNode(transition.getAction().getNewState(), history.getCurrent(), stack.getStackContent(), tape.getStep(), isBranchingTransition);
        history.getCurrent().addChild(child);
        history.setCurrent(child);
    }

    public ControlState getCurrentState() {
        return currentState;
    }


    public void setCurrentStateToInitial() {
        currentState = loadedDefinition.getInitialState();
    }

    public boolean hasAccepted() {
        if (tape.hasFinished()) {
            if (loadedDefinition.isAcceptByFinalState()) {
                if (currentState.isAccepting()) {
                    return true;
                }
            } else {
                if (stack.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setCurrentState(ControlState current) {
        this.currentState = current;
    }


    public void createComputationHistoryStore(ControlState controlState, ArrayList<Character> stackState, int headPosition, boolean moreChildren) {
        ConfigurationNode root = new ConfigurationNode(controlState, null, stackState, headPosition, moreChildren);
        history = new ComputationalTree(root);
    }

    public ComputationalTree getHistory() {
        return history;
    }
}
