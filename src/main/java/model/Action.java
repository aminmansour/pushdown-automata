package model;

public class Action {

    private ControlState newState;
    private Character elementToPush;

    public Action(ControlState newState, Character elementToPush){
        this.newState = newState;
        this.elementToPush = elementToPush;
    }

    public Action() {
    }

    public Character getElementToPush() {
        return elementToPush;
    }

    public ControlState getNewState() {
        return newState;
    }

    public void setNewState(ControlState newState) {
        this.newState = newState;
    }

    public void setElementToPush(Character elementToPush) {
        this.elementToPush = elementToPush;
    }

}
