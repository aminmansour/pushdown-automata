package model;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import java.util.*;

public class PDAMachine {
    private Definition loadedDefinition;
    private InputTape tape;
    private PushDownStack stack;
    private ControlState currentState;
    private ComputationalTree history;
    private boolean isSavedInMemory;

    private ListMultimap<String, Transition> stateToTransitionMap;
    private boolean modifiable;

    public PDAMachine(Definition defToLoad) {
        loadDefinition(defToLoad);
    }

    private void loadDefinition(Definition definition){
        loadedDefinition = definition;
        tape = new InputTape();
        modifiable = true;
        stack = new PushDownStack();
        tape.clear();
        stack.clear();
        sortStateToTransitionMapping();
        getNonDeterministicTransitions();

    }

    public Set<Transition> getNonDeterministicTransitions() {
        Set<Transition> deterministicTransitions = new HashSet<>();
        for (ControlState state : loadedDefinition.getStates()) {
            int indexI = 0;
            Collection<Transition> stateTransitions = stateToTransitionMap.get(state.getLabel());
            for (Transition transition : stateTransitions) {
                Character currentInput = transition.getConfiguration().getInputSymbol();
                Character currentStackSymbol = transition.getConfiguration().getTopElement();
                int indexJ = 0;
                for (Transition transitionToCompare : stateTransitions) {
                    if (indexI != indexJ) {
                        Character inputToCompare = transitionToCompare.getConfiguration().getInputSymbol();
                        Character stackSymbolToCompare = transitionToCompare.getConfiguration().getTopElement();
                        if (currentInput == inputToCompare && currentStackSymbol == stackSymbolToCompare) {
                            deterministicTransitions.add(transition);
                            deterministicTransitions.add(transitionToCompare);
                        } else if (currentInput == '/' && currentStackSymbol == stackSymbolToCompare) {
                            deterministicTransitions.add(transition);
                            deterministicTransitions.add(transitionToCompare);
                        } else if (currentInput == '/' && currentStackSymbol == '/') {
                            deterministicTransitions.add(transition);
                            deterministicTransitions.add(transitionToCompare);
                        }
                    }
                    indexJ++;
                }
                indexI++;
            }
        }
        return deterministicTransitions;
    }

    private void sortStateToTransitionMapping() {
        stateToTransitionMap = MultimapBuilder.hashKeys().arrayListValues().build();
        for (Transition transition : loadedDefinition.getTransitions()) {
            stateToTransitionMap.put(transition.getConfiguration().getState().getLabel(), transition);
        }
    }

    public List<Transition> retrieveTransitionsByState(ControlState state) {
        return stateToTransitionMap.get(state.getLabel());
    }

    public ArrayList<Transition> getPossibleTransitions(ControlState state, Character input, Character stackSymbol) {
        ArrayList<Transition> possibleTransitions = new ArrayList<>();

        for (Transition transition : retrieveTransitionsByState(state)) {

            Character inputSym = transition.getConfiguration().getInputSymbol();
            Character stackSym = transition.getConfiguration().getTopElement();
            if (
                    (inputSym == '/' && stackSym == '/') ||
                            (inputSym == '/' && stackSym == stackSymbol) ||
                            (inputSym == input && stackSym == '/') ||
                            (inputSym == input && stackSym == stackSymbol)) {
                possibleTransitions.add(transition);
            }
        }
        return possibleTransitions;
    }

    public ArrayList<Transition> getPossibleTransitionsFromCurrent() {
        return getPossibleTransitions(currentState, tape.getSymbolAtHead(), stack.top());
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
        tape.readSymbol(transition.getConfiguration().getInputSymbol() == '/');

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
                history.getCurrent(), new ArrayList<>(stack.getStackContent()), tape.getHeadPosition(), tape.getStep(), tape.getRemainingInputAsString(), totalChildren);
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


    public void createComputationHistoryStore(ControlState controlState, ArrayList<Character> stackState, int headPosition, int step, int totalChildren) {
        ConfigurationNode root = new ConfigurationNode(controlState, null, stackState, headPosition, step, tape.getRemainingInputAsString(), totalChildren);
        root.markInPath(true);
        history = new ComputationalTree(root);
    }

    public ComputationalTree getHistory() {
        return history;
    }


    public String getCurrentSequence(boolean isAccepted) {
        ConfigurationNode pointer = history.getRoot();
        StringBuilder output = new StringBuilder(getConfigurationStringFromPreviousState(pointer));
        while ((pointer = pointer.getNextChildInPath()) != null) {
            output.append(" > ").append(getConfigurationStringFromPreviousState(pointer));
        }
        output.append(isAccepted ? " - Accepted" : " - Stuck ");
        return output.toString();
    }

    private String getConfigurationStringFromPreviousState(ConfigurationNode pointer) {
        String originalWord = tape.getOriginalWord();

        String remainingInputString = "";
        if (pointer.getHeadPosition() < originalWord.length()) {
            remainingInputString = originalWord.substring(pointer.getHeadPosition());
        }
        return "( " + pointer.getState().getLabel() + " , " + (remainingInputString.isEmpty() ? " - " : remainingInputString) + " , " + pointer.getStackStateInStringFormat() + " ) ";
    }


    public boolean isSavedInMemory() {
        return isSavedInMemory;
    }


    public void markAsSavedInMemory() {
        isSavedInMemory = true;
    }

    public boolean isNonDeterministic() {
        return getNonDeterministicTransitions().size() > 0;
    }

    public void moveTransitionToNewSource(ControlState oldSourceState, ControlState newSourceState, Transition transition) {
        stateToTransitionMap.get(oldSourceState.getLabel()).remove(transition);
        stateToTransitionMap.get(newSourceState.getLabel()).add(transition);
    }

    public void addTransition(Transition newTransition) {
        loadedDefinition.addTransition(newTransition);
        stateToTransitionMap.get(newTransition.getConfiguration().getState().getLabel()).add(newTransition);
    }

}
