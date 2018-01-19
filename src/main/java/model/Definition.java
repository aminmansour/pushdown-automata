package model;

import com.google.common.collect.*;
import java.util.*;

public class Definition {
    private String identifier;
    private ArrayList<Character> inputAlphabet;
    private ArrayList<ControlState> states;
    private boolean isAcceptByFinalState; //false signifies accept-by-empty-stack
    private ArrayList<Transition> transitions;
    private ListMultimap<String, Transition> stateToTransitionMap;
    private Set<Transition> deterministicTransitions;
    private ControlState initialState;

    public Definition(String identifier, ArrayList<Character> inputAlphabet, ArrayList<ControlState> states, boolean isAcceptByFinalState, ArrayList<Transition> transitions) {
        this.identifier = identifier;
        this.inputAlphabet = inputAlphabet;
        this.states = states;
        this.isAcceptByFinalState = isAcceptByFinalState;
        this.transitions = transitions;
        sortStateToTransitionMapping();
        identifyDeterministicTransitions();
    }

    public static void main(String[] args) {
        ArrayList<Transition> transitions = new ArrayList<>();
        ArrayList<ControlState> states = new ArrayList<>();
        states = new ArrayList<>();
        ControlState q1 = new ControlState("q1", true);
        states.add(q1);
        ControlState q2 = new ControlState("q2",  true);
        states.add(q2);
        Transition transition1 = new Transition(new Configuration(q1, '1', 'A'), new Action(q1, 'A'));
        transitions.add(transition1);
        Transition transition2 = new Transition(new Configuration(q1, '1', 'A'), new Action(q2, 'A'));
        transitions.add(transition2);
        Transition transition3 = new Transition(new Configuration(q2, '1', 'A'), new Action(q1, 'A'));
        transitions.add(transition3);
        Transition transition4 = new Transition(new Configuration(q1, null, 'A'), new Action(q1, 'A'));
        transitions.add(transition4);
        Definition def = new Definition("1", null, states, false, transitions);
        Set<Transition> computatedSet = def.getDeterministicTransitions();
        def.isDeterministic();
        def.getDeterministicTransitions();


    }

    public boolean isDeterministic(){
        return !deterministicTransitions.isEmpty();
    }

    public Set<Transition> getDeterministicTransitions(){
        return deterministicTransitions;
    }


    private void identifyDeterministicTransitions() {
        deterministicTransitions = new HashSet<>();
            for(ControlState state : states) {
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
                            }

                            if (currentInput == null && currentStackSymbol == stackSymbolToCompare) {
                                deterministicTransitions.add(transition);
                                deterministicTransitions.add(transitionToCompare);
                            }
                        }
                        indexJ++;
                    }
                    indexI++;
                }
            }
    }

    private void sortStateToTransitionMapping(){
        stateToTransitionMap = MultimapBuilder.hashKeys().arrayListValues().build();
        for (Transition transition : transitions) {
            stateToTransitionMap.put(transition.getConfiguration().getState().getLabel(), transition);
        }
    }

    public void setInitialState(ControlState controlState) {
        initialState = controlState;
    }


    public ControlState getInitialState() {
        return initialState;
    }

    public List<Transition> getTransitionsByState(ControlState state) {
        return stateToTransitionMap.get(state.getLabel());
    }

    public List<Transition> getPossibleTransitions(ControlState state,Character input,Character stackSymbol){
        List<Transition> possibleTransitions = new ArrayList<>();
        for(Transition transition : getTransitionsByState(state)){
            Character inputSym = transition.getConfiguration().getInputSymbol();
            Character stackSym = transition.getConfiguration().getTopElement();
            if(
                    (inputSym == null && stackSym == null)||
                    (inputSym == null && stackSym == stackSymbol)||
                    (inputSym == input && stackSym == null)||
                    (inputSym == input && stackSym == stackSymbol))
            {
                possibleTransitions.add(transition);
            }
        }
        return possibleTransitions;
    }
}
