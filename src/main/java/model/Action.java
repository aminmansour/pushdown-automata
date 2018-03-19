package model;

/**
 * Object class which represents the resulting effect of the application of the 'Transition' for which this action is
 * stored within.
 */
public class Action {

    private ControlState newState;
    private Character elementToPush;

    /**
     * The in-depth constructor of action object
     *
     * @param newState      the resulting state
     * @param elementToPush the resulting stack symbol at the top of stack
     */
    public Action(ControlState newState, Character elementToPush) {
        this.newState = newState;
        this.elementToPush = elementToPush;
    }

    /**
     * The basic constructor of an action object
     */
    public Action() {
    }

    /**
     * @return returns the resulting stack symbol
     */
    public Character getElementToPush() {
        return elementToPush;
    }

    /**
     * @param elementToPush the new resulting element to push
     */
    public void setElementToPush(Character elementToPush) {
        this.elementToPush = elementToPush;
    }

    /**
     * @return returns the resulting state
     */
    public ControlState getNewState() {
        return newState;
    }

    /**
     * @param newState the new resulting state
     */
    public void setNewState(ControlState newState) {
        this.newState = newState;
    }

}
