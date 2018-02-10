package model;

public class Transition {
    private Action action;
    private Configuration configuration;


    public Transition(Configuration configuration,Action action) {
        this.action = action;
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Action getAction() {
        return action;
    }

    public ControlState getSourceState() {
        return configuration.getState();
    }

    public ControlState getTargetState() {
        return action.getNewState();
    }

    @Override
    public String toString() {
        return configuration.getState().getLabel() + "," + configuration.getTopElement() + " -> " + action.getElementToPush();
    }
}
