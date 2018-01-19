package controller;

import model.*;

import java.util.List;

public class PDAController {
    private boolean gameOver;
    private Definition loadedDefinition;
    private InputTape tape;
    private PushDownStack stack;
    private ControlState currentState;
    private ComputationalTree history;

    public PDAController(Definition defToLoad){
        loadDefinition(defToLoad);
    }

    private void loadDefinition(Definition definition){
        gameOver = false;
        loadedDefinition = definition;
        currentState = loadedDefinition.getInitialState();
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


    private void stepForward(){
        List<Transition> transitions = loadedDefinition.getPossibleTransitions(currentState, tape.getSymbolAtHead(), stack.top());
        for (int i = 0; i < transitions.size(); i++) {
//            if(checkIfReadyExplored(transitions.get(i).getConfiguration())) {
//                executeTransition(transitions.get(0), transitions.size());
//            }

        }
    }



}
