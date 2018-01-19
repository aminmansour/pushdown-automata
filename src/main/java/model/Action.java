package model;

public class Action {
    private ControlState newState;
    private Character elementToPush;

    public Action(ControlState newState, Character elementToPush){
        this.newState = newState;
        this.elementToPush = elementToPush;
    }

    public Character getElementToPush() {
        return elementToPush;
    }

    public ControlState getNewState() {
        return newState;
    }
}
