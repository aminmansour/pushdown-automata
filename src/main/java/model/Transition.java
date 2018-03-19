package model;

/**
 * Object class which represents a transition which can be executed in computation
 */
public class Transition {

    private Configuration configuration;
    private Action action;

    /**
     * An in-depth constructor of a transition object
     *
     * @param configuration the configuration required
     * @param action        the resulting action
     */
    public Transition(Configuration configuration, Action action) {
        this.action = action;
        this.configuration = configuration;
    }

    /**
     * An empty constructor of a transition object
     */
    public Transition() { }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Action getAction() {
        return action;
    }

    public ControlState retrieveSourceState() {
        return configuration.getState();
    }

    public ControlState retrieveTargetState() {
        return action.getNewState();
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String toString() {
        return configuration.getState().getLabel() + "," + configuration.getTopElement() + " -> " + action.getElementToPush();
    }
}
