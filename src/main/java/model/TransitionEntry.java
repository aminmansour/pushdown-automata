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

    public TransitionEntry(String currentState, String elementAtHead, String topOfStack, String resultingState, String resultingTopOfStack) {
        this.currentState = currentState;
        this.elementAtHead = elementAtHead;
        this.topOfStack = topOfStack;
        this.resultingState = resultingState;
        this.resultingTopOfStack = resultingTopOfStack;
    }

    @Override
    public boolean equals(Object obj) {

        return currentState.equals(((TransitionEntry) obj).currentState) &&
                elementAtHead.equals(((TransitionEntry) obj).elementAtHead) &&
                topOfStack.equals(((TransitionEntry) obj).topOfStack) &&
                resultingState.equals(((TransitionEntry) obj).resultingState) &&
                resultingTopOfStack.equals(((TransitionEntry) obj).resultingTopOfStack);

    }
}