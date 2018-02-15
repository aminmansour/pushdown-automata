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


    public void executeTransition(Transition transition, int totalChildren) {
        stack.loadState(new ArrayList<>(stack.getStackContent()));
        tape.readSymbol();
        if (transition.getConfiguration().getTopElement() != '/') {
            stack.pop();
        }
        if (transition.getAction().getElementToPush() != '/') {
            stack.push(transition.getAction().getElementToPush());
        }
        currentState = transition.getAction().getNewState();
        addConfigurationStateToHistory(transition, totalChildren);
    }

    private void addConfigurationStateToHistory(Transition transition, int totalChildren) {
        ConfigurationNode child = new ConfigurationNode(transition.getAction().getNewState(),
                history.getCurrent(), new ArrayList<>(stack.getStackContent()), tape.getStep(), totalChildren);
        history.getCurrent().addChild(child);
        history.setCurrent(child);
        child.markInPath(true);
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


    public void createComputationHistoryStore(ControlState controlState, ArrayList<Character> stackState, int headPosition, int totalChildren) {
        ConfigurationNode root = new ConfigurationNode(controlState, null, stackState, headPosition, totalChildren);
        root.markInPath(true);
        history = new ComputationalTree(root);
    }

    public ComputationalTree getHistory() {
        return history;
    }


    public String getCurrentSequence(boolean isAccepted) {
        ConfigurationNode pointer = history.getRoot();
        String output = getConfigurationStringFromPreviousState(pointer);
        while ((pointer = pointer.getNextChildInPath()) != null) {
            output += " > " + getConfigurationStringFromPreviousState(pointer);
        }
        output += isAccepted ? " - Accepted" : " - Stuck ";
        return output;
    }

    private String getConfigurationStringFromPreviousState(ConfigurationNode pointer) {
        String remainingInputString = tape.getOriginalWord().substring(pointer.getHeadPosition());
        return "( " + pointer.getState().getLabel() + " , " + (remainingInputString.isEmpty() ? " - " : remainingInputString) + " , " + pointer.getStackStateInStringFormat() + " ) ";
    }
}
