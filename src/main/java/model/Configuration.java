package model;

/**
 * Object class which represents the initial prerequisite of the application of the 'Transition' for which this Configuration is
 * stored within.
 */
public class Configuration {
    private ControlState state;
    private Character inputSymbol;
    private Character topElement;

    /**
     * The in-depth constructor of action object
     *
     * @param state       the current state
     * @param inputSymbol the input symbol to be read
     * @param topElement  the stack symbol to pop
     */
    public Configuration(ControlState state, Character inputSymbol, Character topElement) {
        this.state = state;
        this.inputSymbol = inputSymbol;
        this.topElement = topElement;
    }

    /**
     * The empty constructor of an Configuration object
     */
    public Configuration() { }

    public ControlState getState() {
        return state;
    }

    public Character getInputSymbol() {
        return inputSymbol;
    }

    public Character getTopElement() {
        return topElement;
    }

    public void setState(ControlState state) {
        this.state = state;
    }

    public void setInputSymbol(Character inputSymbol) {
        this.inputSymbol = inputSymbol;
    }

    public void setTopElement(Character topElement) {
        this.topElement = topElement;
    }
}
