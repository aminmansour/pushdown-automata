package model;

import java.util.List;

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
        history = new ComputationalTree();
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
        ConfigurationNode configNode = new ConfigurationNode(config,history.getCurrent());
        history.addChildToCurrent(config,totalChildren);
    }


    public List<Transition> getPossibleTransitionsFromCurrent() {
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


    public void executeTransition(Transition transition) {
        tape.readSymbol();
        if (transition.getConfiguration().getTopElement() != '/') {
            stack.pop();
        }
        if (transition.getAction().getElementToPush() != '/') {
            stack.push(transition.getAction().getElementToPush());
        }
        currentState = transition.getAction().getNewState();
    }

    public ControlState getCurrentState() {
        return currentState;
    }


}
