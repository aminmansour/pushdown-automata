package model;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.org.apache.xpath.internal.operations.String;


import java.util.ArrayList;
import java.util.List;

public class ModelFactory {

    public static ArrayList<Definition> definitions;

    public static boolean checkForDefinitionOccurrence(Definition definition) {
        for (Definition d : Memory.load()) {
            if (definition.getIdentifier().equals(d.getIdentifier())) {
                return true;
            }
        }
        return false;
    }


    public static ControlState checkForStateOccurrence(List<ControlState> states, String stateLabel) {
        for (ControlState controlState : states) {
            if (controlState.getLabel().equals(stateLabel)) {
                return controlState;
            }
        }
        return null;
    }

    public static boolean checkIfAcceptingState(ControlState state, String[] acceptingStates) {
        for (String acceptingState : acceptingStates) {
            if (acceptingState.equals(state.getLabel())) {
                return true;
            }
        }
        return false;
    }

}
