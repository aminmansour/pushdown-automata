package model;

/**
 * ControlState represent the theoretical control states found in a PDA
 */
public class ControlState {
    private boolean isAccepting;
    private boolean isInitial;
    private String label;

    /**
     * An empty constructor of a ControlState instance
     */
    public ControlState() {
    }


    /**
     * An in-depth constructor of a ControlState instance
     */
    public ControlState(String label) {
        this.isAccepting = false;
        this.isInitial = false;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isAccepting() {
        return isAccepting;
    }

    public boolean isInitial() {
        return isInitial;
    }

    /**
     * A method which marks the control state instance as an accepting one
     */
    public void markAsAccepting() {
        isAccepting = true;
    }

    /**
     * A method which marks the control state instance as an initial one
     */
    public void markAsInitial() {
        isInitial = true;
    }

    @Override
    public boolean equals(Object obj) {
        return label.equals(((ControlState) obj).label);
    }
}
