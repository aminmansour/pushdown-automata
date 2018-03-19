package model;


/**
 * Object class which holds information of a Transition instance in string format. This is only used
 * with the purpose of being stored in a TableView. Each instance is linked to a transition
 */
public class TransitionTableEntry {

    //info fields
    private String currentState;
    private String elementAtHead;
    private String topOfStack;
    private String resultingState;
    private String resultingTopOfStack;
    private Transition transition;


    public TransitionTableEntry(String currentState, String elementAtHead, String topOfStack, String resultingState, String resultingTopOfStack) {
        this.currentState = currentState;
        this.elementAtHead = elementAtHead;
        this.topOfStack = topOfStack;
        this.resultingState = resultingState;
        this.resultingTopOfStack = resultingTopOfStack;

    }

    /**
     * A method which checks for equivalence
     * @param obj the object to be compared with
     * @return true if the two objects are equivalent, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return obj != null && currentState.equals(((TransitionTableEntry) obj).currentState) &&
                elementAtHead.equals(((TransitionTableEntry) obj).elementAtHead) &&
                topOfStack.equals(((TransitionTableEntry) obj).topOfStack) &&
                resultingState.equals(((TransitionTableEntry) obj).resultingState) &&
                resultingTopOfStack.equals(((TransitionTableEntry) obj).resultingTopOfStack);

    }

    /**
     * A method which comapres the equivalence of a transition with an transition entry
     * @param transition the transition object to be compared with
     * @return true if the two are equivalent, false otherwise
     */
    public boolean sameAs(Transition transition) {
        return transition.getConfiguration().getState().getLabel().equals(currentState) &&
                transition.getConfiguration().getInputSymbol().toString().equals(elementAtHead) &&
                transition.getConfiguration().getTopElement().toString().equals(topOfStack) &&
                transition.getAction().getNewState().getLabel().equals(resultingState) &&
                transition.getAction().getElementToPush().toString().equals(resultingTopOfStack);
    }

    public void setTransition(Transition transition) {
        this.transition = transition;
    }

    public Transition getTransition() {
        return transition;
    }

    /**
     * Retrieves the latest information from the transition which this TransitionTableEntry instance
     * is linked to
     */
    public void update() {
        this.currentState = transition.getConfiguration().getState().getLabel();
        this.elementAtHead = transition.getConfiguration().getInputSymbol().toString();
        this.topOfStack = transition.getConfiguration().getTopElement().toString();
        this.resultingState = transition.getAction().getNewState().getLabel();
        this.resultingTopOfStack = transition.getAction().getElementToPush().toString();
    }

    @Override
    public String toString() {
        return currentState + "  ,  " + elementAtHead + "  ,  " + topOfStack + "  ->  " + resultingState + "  ,  " + resultingTopOfStack;
    }

    public String getCurrentState() {
        return currentState;
    }

    public String getElementAtHead() {
        return elementAtHead;
    }

    public String getTopOfStack() {
        return topOfStack;
    }

    public String getResultingState() {
        return resultingState;
    }

    public String getResultingTopOfStack() {
        return resultingTopOfStack;
    }

}