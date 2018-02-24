package model;

public class TransitionEntry {

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

    private String currentState;
    private String elementAtHead;
    private String topOfStack;
    private String resultingState;
    private String resultingTopOfStack;
    private Transition transition;

    public TransitionEntry(String currentState, String elementAtHead, String topOfStack, String resultingState, String resultingTopOfStack) {
        this.currentState = currentState;
        this.elementAtHead = elementAtHead;
        this.topOfStack = topOfStack;
        this.resultingState = resultingState;
        this.resultingTopOfStack = resultingTopOfStack;

    }

    @Override
    public boolean equals(Object obj) {


        return obj != null && currentState.equals(((TransitionEntry) obj).currentState) &&
                elementAtHead.equals(((TransitionEntry) obj).elementAtHead) &&
                topOfStack.equals(((TransitionEntry) obj).topOfStack) &&
                resultingState.equals(((TransitionEntry) obj).resultingState) &&
                resultingTopOfStack.equals(((TransitionEntry) obj).resultingTopOfStack);

    }

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
}