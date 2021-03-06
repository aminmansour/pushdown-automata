package model;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import java.util.*;

/**
 * A PDAMachine represents the functionality that can be carried out on a set definition that has been loaded. Its responsible for
 * interacting with the states and transitions.
 */
public class PDAMachine {

    //components
    private Definition loadedDefinition;
    private InputTape tape;
    private PushDownStack stack;
    private ControlState currentState;
    private ExecutionTree executionTree;
    private boolean isSavedInMemory;
    private ListMultimap<String, Transition> stateToTransitionMap;

    /**
     * A PDAMachine instant constructor which loads the definition provided as a parameter
     *
     * @param defToLoad the definition that will be loaded
     */
    public PDAMachine(Definition defToLoad) {
        loadDefinition(defToLoad);
    }

    private void loadDefinition(Definition definition){
        loadedDefinition = definition;
        tape = new InputTape();
        stack = new PushDownStack();
        tape.clear();
        stack.clear();
        sortStateToTransitionMapping();
    }

    /**
     * A method which identifies all the non-deterministic transitions found in the PDA
     * @return A set of all non-deterministic transitions found in the current PDA definition
     */
    public Set<Transition> getNonDeterministicTransitions() {
        Set<Transition> nonDeterministicTransitions = new HashSet<>();
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
                        if ((currentInput == inputToCompare || currentInput == '/' || inputToCompare == '/') && currentStackSymbol == stackSymbolToCompare) {
                            nonDeterministicTransitions.add(transition);
                            nonDeterministicTransitions.add(transitionToCompare);
                        }
                    } else {
                        if (currentInput == '/') {
                            nonDeterministicTransitions.add(transition);
                        }
                    }
                    indexJ++;
                }
                indexI++;
            }
        }
        return nonDeterministicTransitions;
    }

    //relates each transition to its source control states, allowing for quicker retrieval and lookup of transitions down the line
    private void sortStateToTransitionMapping() {
        stateToTransitionMap = MultimapBuilder.hashKeys().arrayListValues().build();
        for (Transition transition : loadedDefinition.getTransitions()) {
            stateToTransitionMap.put(transition.getConfiguration().getState().getLabel(), transition);
        }
    }


    //A method which receives all possible transitions that can be made from the control state provided
    private List<Transition> retrieveTransitionsFromSourceState(ControlState sourceState) {
        return stateToTransitionMap.get(sourceState.getLabel());
    }

    /**
     * A method which receives all possible transitions that can be made from the configuration specified.
     * @param state the source control state
     * @param input the input symbol to be read
     * @param stackSymbol the stack symbol to pop
     * @return A list of transitions which match the parameters provided
     */
    private ArrayList<Transition> getPossibleTransitions(ControlState state, Character input, Character stackSymbol) {
        ArrayList<Transition> possibleTransitions = new ArrayList<>();

        for (Transition transition : retrieveTransitionsFromSourceState(state)) {

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

    /**
     * A method which receives all possible transitions that can be made from the current state of the PDA.
     * @return A list of transitions that can be made from the current state
     */
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


    /**
     * A method which executes the transition on the current state of the PDA.
     * @param transition the transition to execute
     * @param totalChildren the total children/siblings
     */
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
        addResultingConfigurationContextToExecutionTree(transition, totalChildren);
    }

    //adds the resulting ConfigurationContext to the execution tree
    private void addResultingConfigurationContextToExecutionTree(Transition transition, int totalChildren) {
        ConfigurationContext child = new ConfigurationContext(transition.getAction().getNewState(),
                executionTree.getCurrent(), new ArrayList<>(stack.getStackContent()), tape.getHeadPosition(), tape.getStep(), totalChildren);
        executionTree.getCurrent().addChild(child);
        executionTree.setCurrent(child);
        child.markInPath(true);
    }

    public ControlState getCurrentState() {
        return currentState;
    }


    /**
     * A method which sets the current state to the initial state
     */
    private void setCurrentStateToInitial() {
        currentState = loadedDefinition.getInitialState();
    }

    /**
     * A method which checks if that the current PDA has met the accepting criterion
     *
     * @return true, if the pda has finished and has accepted the input, otherwise false
     */
    public boolean isAccepted() {
        if (tape.isFinished()) {
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

    /**
     * A method which sets the current state
     * @param current the state to set to current
     */
    private void setCurrentState(ControlState current) {
        this.currentState = current;
    }


    private void createNewExecutionTree(ControlState controlState, ArrayList<Character> stackState,
                                        int headPosition, int step, int totalChildren) {
        ConfigurationContext root = new ConfigurationContext(controlState, null, stackState, headPosition, step, totalChildren);
        root.markInPath(true);
        executionTree = new ExecutionTree(root);
    }

    public ExecutionTree getExecutionTree() {
        return executionTree;
    }


    /**
     * A method which gets the sequence of configurations in the current execution path
     *
     * @param isAccepted boolean value which determines if a solution is found or not
     * @return sequence of configurations in the current execution path in string format
     */
    public String getCurrentExecutionSequence(boolean isAccepted) {
        ConfigurationContext pointer = executionTree.getRoot();
        StringBuilder output = new StringBuilder(printOutFromContext(pointer));
        while ((pointer = pointer.getNextChildInPath()) != null) {
            output.append(" > ").append(printOutFromContext(pointer));
        }
        output.append(isAccepted ? " - Accepted" : " - Stuck ");
        return output.toString();
    }


    //prints out in string format a string which represents the ConfigurationContext
    private String printOutFromContext(ConfigurationContext pointer) {
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

    /**
     * A method which changes the source of the transition's initial state to the new state
     *
     * @param oldSourceState old initial state
     * @param newSourceState new initial state
     * @param transition     the transition to modify
     */
    public void moveTransitionSourceState(ControlState oldSourceState, ControlState newSourceState, Transition transition) {
        stateToTransitionMap.get(oldSourceState.getLabel()).remove(transition);
        stateToTransitionMap.get(newSourceState.getLabel()).add(transition);
    }

    /**
     * A method which adds a new transition to the current definition loaded
     * @param newTransition the new transition to add
     */
    public void addTransition(Transition newTransition) {
        loadedDefinition.addTransition(newTransition);
        stateToTransitionMap.get(newTransition.getConfiguration().getState().getLabel()).add(newTransition);
    }


    /**
     * A method which loads a input into current pda
     *
     * @param input the input to load
     */
    public void loadInput(String input) {
        tape.clear();
        stack.clear();
        setCurrentState(getDefinition().getInitialState());
        ArrayList<Character> inputSymbols = new ArrayList<>(input.length());
        for (char c : input.toCharArray()) {
            inputSymbols.add(c);
        }
        tape.setInput(inputSymbols);
        createNewExecutionTree(loadedDefinition.getInitialState(), new ArrayList<>(), 0, 0, getPossibleTransitionsFromCurrent().size());
    }


    /**
     * A method which takes the PDA back to its previous configuration
     */
    public void previous() {
        if (tape.getStep() > 0) {
            executionTree.getCurrent().markInPath(false);
            ConfigurationContext prev = executionTree.getCurrent().getParent();
            loadConfigurationContext(prev);
        }
    }

    /**
     * A method which take a PDA ConfigurationContext which holds a snapshot of data of a certain configuration,
     * and loads it into the current PDA
     *
     * @param context the ConfigurationContext to load
     */
    public void loadConfigurationContext(ConfigurationContext context) {
        executionTree.setCurrent(context);
        currentState = context.getState();
        tape.setHeadPosition(context.getHeadPosition());
        tape.setStep(context.getStep());
        stack.loadState(context.getStackState());
    }

    /**
     * restarts current PDA and cancels computation and removes current input
     */
    public void stop() {
        currentState = null;
        tape.clear();
        stack.clear();
    }

    /**
     * A method which cancels the current computation of the PDA and restarts from
     * the beginning with the same input
     */
    public void redo() {
        tape.setStep(0);
        tape.setHeadPosition(0);
        if (currentState != null) {
            setCurrentStateToInitial();
            createNewExecutionTree(loadedDefinition.getInitialState(), new ArrayList<>(), 0, 0, getPossibleTransitionsFromCurrent().size());
        }
        stack.clear();
    }

    //runs dfs for instant-run on a given input
    public int runInstantRunDFS(boolean isAlternativeSearch) {
        if (isAlternativeSearch) {
            previous();
        }
        int totalMoves = 0;
        boolean toBacktrack = false;
        while (true) {
            ArrayList<Transition> transitions = getPossibleTransitionsFromCurrent();
            if (isAccepted()) {
                return 1;
            } else if (transitions.size() == 0 || toBacktrack) {
                toBacktrack = false;
                while (true) {
                    previous();
                    ArrayList<ConfigurationContext> exploredChildren = getExecutionTree().getCurrent().getExploredChildren();
                    if (exploredChildren.size() > 0 &&
                            exploredChildren.get(0).getTotalSiblings() > exploredChildren.size()) {
                        break;
                    } else if (getTape().getStep() == 0) {
                        return 2;
                    }
                }
            } else {
                toBacktrack = true;
                java.util.Collections.shuffle(transitions);
                transitionsLoop:
                for (Transition transition : transitions) {
                    ConfigurationContext previousState = checkIfPresentInHistory(transition);
                    if (previousState == null) {
                        executeTransition(transition, transitions.size());
                        toBacktrack = false;
                        totalMoves++;
                        if (totalMoves % 20 == 0) {
                            return 3;
                        }
                        break transitionsLoop;
                    }
                }

            }
        }
    }


    //checks if transition has been executed before. If it has return the
    //ConfigurationContext, else return null.
    public ConfigurationContext checkIfPresentInHistory(Transition transition) {
        ArrayList content = new ArrayList(getStack().getStackContent());
        int headPosition = getTape().getHeadPosition();
        if (transition.getConfiguration().getInputSymbol() != '/') {
            headPosition++;
        }
        if (transition.getConfiguration().getTopElement() != '/') {
            content.remove(content.size() - 1);
        }
        if (transition.getAction().getElementToPush() != '/') {
            content.add(transition.getAction().getElementToPush());
        }
        return getExecutionTree().getCurrent().hasChild(transition.getAction().getNewState(), content, headPosition);
    }

}
