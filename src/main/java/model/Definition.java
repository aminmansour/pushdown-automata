package model;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import java.util.*;

public class Definition {
    private String identifier;
    private ArrayList<Character> inputAlphabet;
    private ArrayList<ControlState> states;
    private boolean isAcceptByFinalState; //false signifies accept-by-empty-stack

    public ArrayList<ControlState> getStates() {
        return states;
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }

    private ArrayList<Transition> transitions;
    private ListMultimap<String, Transition> stateToTransitionMap;
    private Set<Transition> deterministicTransitions;
    private ControlState initialState;

    public Definition(String identifier, ArrayList<ControlState> states, ControlState initialState, ArrayList<Transition> transitions, boolean isAcceptByFinalState) {
        this.identifier = identifier;
//        this.inputAlphabet = inputAlphabet;
        this.states = states;
        this.isAcceptByFinalState = isAcceptByFinalState;
        this.transitions = transitions;
        this.initialState = initialState;
        initialState.markAsInitial();
        sortStateToTransitionMapping();
        identifyDeterministicTransitions();
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


    public ControlState getInitialState() {
        return initialState;
    }

    public List<Transition> getTransitionsByState(ControlState state) {
        return stateToTransitionMap.get(state.getLabel());
    }

    public List<Transition> getPossibleTransitions(ControlState state,Character input,Character stackSymbol){
        List<Transition> possibleTransitions = new ArrayList<>();

        for(Transition transition : getTransitionsByState(state)){
            System.out.println(transition);
            Character inputSym = transition.getConfiguration().getInputSymbol();
            Character stackSym = transition.getConfiguration().getTopElement();
            if(
                    (inputSym == '/' && stackSym == '/') ||
                            (inputSym == '/' && stackSym == stackSymbol) ||
                            (inputSym == input && stackSym == '/') ||
                    (inputSym == input && stackSym == stackSymbol))
            {
                possibleTransitions.add(transition);
            }
        }
        return possibleTransitions;
    }
}
