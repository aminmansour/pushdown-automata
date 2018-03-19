package model;

/**
 * A data structure responsible for storing a store of previously executed transitions
 * via ConfigurationContext instance objects (Works in the same way as a tree DST does)
 */
public class ExecutionTree {

    private ConfigurationContext root;
    private ConfigurationContext current;

    /**
     * A constructor for an ExecutionTree object
     *
     * @param root the root state of PDA machine stored in the form of a ConfigurationContext
     */
    public ExecutionTree(ConfigurationContext root) {
        this.root = root;
        current = root;
    }


    /**
     * @return The root/initial state of the pushdown-automata
     */
    public ConfigurationContext getRoot() {
        return root;
    }

    /**
     * @return The current state of the pushdown
     */
    public ConfigurationContext getCurrent() {
        return current;
    }

    /**
     * Updates the current recorded state of the pushdown, in terms of step,stack content, and tape content
     *
     * @param current the current state
     */
    public void setCurrent(ConfigurationContext current) {
        this.current = current;
    }
}
