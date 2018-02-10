package model;

public class ControlState {
    private boolean isAccepting;
    private boolean isInitial;
    private String label;


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

    public void markAsAccepting() {
        isAccepting = true;
    }

    public void markAsInitial() {
        isInitial = true;
    }

}
