package model;

public class Configuration {
    private ControlState state;
    private Character inputSymbol;
    private Character topElement;

    public Configuration(ControlState state, Character inputSymbol, Character topElement) {
        this.state = state;
        this.inputSymbol = inputSymbol;
        this.topElement = topElement;
    }

    public ControlState getState() {
        return state;
    }

    public Character getInputSymbol() {
        return inputSymbol;
    }

    public Character getTopElement() {
        return topElement;
    }
}
