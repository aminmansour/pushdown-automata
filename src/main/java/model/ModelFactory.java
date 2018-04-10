package model;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.org.apache.xpath.internal.operations.String;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ModelFactory is a suite which is in charge of common model manipulation operations found in the
 * application
 */
public class ModelFactory {

    public static ArrayList<Definition> libraryStore;
    public static ArrayList<Definition> exampleStore;


    /**
     * A method which checks if current definition instance provided is ready stored in memory
     * by comparing its identifier with the library collection
     *
     * @param definition the definition to check
     * @return true, if the definition instance has an identifier which matches a identifier in the collection, otherwise false
     */
    public static boolean checkForDefinitionOccurrence(Definition definition) {
        for (Definition d : MemoryFactory.loadLibrary()) {
            if (definition.getIdentifier().equals(d.getIdentifier())) {
                return true;
            }
        }
        return false;
    }

    /**
     * A method which looks for a state via its label and retrieves it if present in the control states list
     *
     * @param states     the list of control states to search within
     * @param stateLabel the label to search for
     * @return the control state for which the state label belongs to, otherwise return null if a state with the label specified doesn't exist
     */
    public static ControlState stateLookup(List<ControlState> states, String stateLabel) {
        for (ControlState controlState : states) {
            if (controlState.getLabel().equals(stateLabel)) {
                return controlState;
            }
        }
        return null;
    }


    /**
     * A method which duplicates a definition instance
     *
     * @param definition the definition instance to duplication
     * @return the definition copy
     */
    public static Definition duplicate(Definition definition) {
        ArrayList<ControlState> statesCopy = new ArrayList<>();
        ArrayList<Transition> transitionsCopy = new ArrayList<>();
        ControlState initialStateCopy = null;
        Map<String, ControlState> stateIndex = new TreeMap<>();
        for (ControlState state : definition.getStates()) {
            ControlState controlState = new ControlState(state.getLabel());
            stateIndex.put(controlState.getLabel(), controlState);
            if (state.isInitial()) {
                initialStateCopy = controlState;
                controlState.markAsInitial();
            }

            if (state.isAccepting()) {
                controlState.markAsAccepting();
            }
            statesCopy.add(controlState);

        }
        for (Transition transition : definition.getTransitions()) {
            Configuration configurationCopy = new Configuration(stateIndex.get(transition.getConfiguration().getState().getLabel()),
                    transition.getConfiguration().getInputSymbol(), transition.getConfiguration().getTopElement());
            Action actionCopy = new Action(stateIndex.get(transition.getAction().getNewState().getLabel()), transition.getAction().getElementToPush());
            Transition transitionCopy = new Transition(configurationCopy, actionCopy);
            transitionsCopy.add(transitionCopy);
        }
        Definition definitionCopy = new Definition(new String(definition.getIdentifier()), statesCopy, initialStateCopy, transitionsCopy, definition.isAcceptByFinalState());
        definitionCopy.setExampleHint(definition.getExampleHint());
        return definitionCopy;
    }
}
