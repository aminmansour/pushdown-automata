package model;

public class ControlState {
    private boolean isAccepting;
    private String label;


    public ControlState(String label, boolean isAccepting) {
        this.isAccepting = isAccepting;
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

    public void setAccepting(boolean accepting) {
        isAccepting = accepting;
    }

}
