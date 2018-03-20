package model;

import java.util.ArrayList;

/**
 * Holds the context/state of the pushdown-automata at any one time.
 */
public class ConfigurationContext {


    //standard fields
    private ArrayList<Character> stackState;
    private int headPosition;


    private ConfigurationContext parent;
    private int step;
    private ArrayList<ConfigurationContext> exploredChildren;

    //flag fields
    private int totalSiblings;
    private boolean isInPath;
    private ControlState controlState;
    private int numberOfVisits;


    /**
     * A in-depth constructor for an ConfigurationContext object
     *
     * @param controlState   current control state
     * @param parent         the parent context before the transition which caused it was applied
     * @param stackState     the stack content
     * @param headPosition   the position of head
     * @param step           the steps made in execution up to ths point
     * @param totalSiblings  he amount of sibling nodes in the ExecutionTree
     */
    public ConfigurationContext(ControlState controlState, ConfigurationContext parent, ArrayList<Character> stackState, int headPosition, int step, int totalSiblings) {
        this.parent = parent;
        this.step = step;
        this.exploredChildren = new ArrayList<>();
        this.stackState = stackState;
        this.headPosition = headPosition;
        this.totalSiblings = totalSiblings;
        this.controlState = controlState;
        numberOfVisits = 1;

    }


    /**
     * @return The parent ConfigurationContext which preceded this Context in the execution.
     */
    public ConfigurationContext getParent() {
        return parent;
    }

    /**
     * Get the children that have been explored for a solution
     *
     * @return An array of 'ConfigurationContext''s
     */
    public ArrayList<ConfigurationContext> getExploredChildren() {
        return exploredChildren;
    }


    /**
     * Adds a resulting context to the current ConfigurationContext collection of children
     *
     * @param child the resulting context on application of a transition
     */
    public void addChild(ConfigurationContext child) {
        exploredChildren.add(child);
    }

    /**
     * @return The control state of the context
     */
    public ControlState getState() {
        return controlState;
    }

    /**
     * @return the content of stack
     */
    public ArrayList<Character> getStackState() {
        return stackState;
    }


    /**
     * @return The head position of tape
     */
    public int getHeadPosition() {
        return headPosition;
    }

    /**
     * A method to define whether the ConfigurationContext instance exists in the execution path
     * @param isInPath boolean value to specify the outcome
     */
    public void markInPath(boolean isInPath) {
        this.isInPath = isInPath;
    }


    /**
     * A method which looks for a ConfigurationContext instance amongst its children which match
     * the context provided in the parameters.
     *
     * @param state        the control state to look for
     * @param stackContent the stack context to look for
     * @param step         the step to look for
     * @return if a ConfigurationContext instance is found then return, else return null
     */
    public ConfigurationContext hasChildWithContext(ControlState state, ArrayList<Character> stackContent, int step) {
        for (ConfigurationContext configuration : exploredChildren) {
            if (state.equals(configuration.controlState) && step == configuration.headPosition && compareStackContent(configuration.stackState, stackContent)) {
                return configuration;
            }
        }
        return null;
    }

    /**
     * A method which checks if the context specification provided via the parameter is the same as
     * the ConfigurationContext instance
     *
     * @param state        the state to look for
     * @param stackContent the stack content to look for
     * @param remaingInput the remaining input in the tape to look for
     * @return if the current instance matches up with the context then return true, else return false
     */
    public boolean hasContext(ControlState state, ArrayList<Character> stackContent, String remaingInput) {
        return state.equals(controlState) && remaingInput.equals(remaingInput) && compareStackContent(stackState, stackContent);
    }

    //compares stack content to see if they are the same
    private boolean compareStackContent(ArrayList<Character> stackState, ArrayList<Character> stackContent) {

        if (stackState.size() != stackContent.size()) {
            return false;
        }

        for (int i = 0; i < stackState.size(); i++) {
            if (stackState.get(i) != stackContent.get(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * A method to return the next ConfigurationContext amongst its children which is in the current execution path
     *
     * @return returns the context if one exists, else returns null
     */
    public ConfigurationContext getNextChildInPath() {
        for (ConfigurationContext configurationContext : exploredChildren) {
            if (configurationContext.isInPath) {
                return configurationContext;
            }
        }
        return null;
    }


    /**
     * A method which takes each stack elements and puts it into a string
     * @return the string of stack elements
     */
    public String getStackStateInStringFormat() {
        if (stackState.isEmpty()) {
            return " - ";
        }
        String output = "";
        for (Character character : stackState) {
            output += character;
        }
        return output;
    }


    /**
     * @return returns the amount of siblings found in the ExecutionTree
     */
    public int getTotalSiblings() {
        return totalSiblings;
    }

    /**
     * @return the step of execution
     */
    public int getStep() {
        return step;
    }

    /**
     * Increments the number of made visits
     *
     * @return rhw roral number of visits in current execution
     */
    public int increment() {
        return numberOfVisits++;
    }

    /**
     * A method which compares two ConfigurationContext instance for equivalence.
     *
     * @param other the other ConfigurationContext instance to compare
     * @return true, if the two are the same in terms of effect, but false otherwise
     */
    public boolean sameAs(ConfigurationContext other) {
        if (stackState.size() != other.stackState.size()) {
            return false;
        }
        for (int i = 0; i < stackState.size(); i++) {
            if (stackState.get(i) != other.stackState.get(i)) {
                return false;
            }
        }

        if (headPosition != other.getHeadPosition()) {
            return false;
        }

        return controlState == other.controlState;
    }


    public int getNumberOfVisits() {
        return numberOfVisits;
    }

    public void setNumberOfVisits(int numberOfVisits) {
        this.numberOfVisits = numberOfVisits;
    }
}
