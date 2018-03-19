package model;

import java.util.ArrayList;

/**
 * PushDownStack represent the theoretical pushdown found in a PDA. Its wrapped around an ArrayList.
 */
public class PushDownStack {
    private ArrayList<Character> stackContent;


    public PushDownStack() {
        this.stackContent = new ArrayList<>();
    }

    /**
     * A method which removes the item at the top of the stack
     *
     * @return the element at the top of the stack, or 0 otherewize if the stack is empty
     */
    public char pop(){
        if(!stackContent.isEmpty()) {
            return stackContent.remove(stackContent.size()-1);
        }
        return 0;
    }

    /**
     * A method which adds a symbol to the top of the stack
     * @param symbol the symbol to add to the current stack
     */
    public void push(char symbol){
        stackContent.add(symbol);
    }

    /**
     * A method which checks that to see if stack is empty
     * @return true if stack is empty, false otherwise
     */
    public boolean isEmpty() {
        return stackContent.size() == 0;
    }

    /**
     * A method which clears the content the content of a stack
     */
    public void clear(){
        stackContent.clear();
    }

    /**
     * A method which returns the element at the top of the stack without removing it
     * @return the element at the top of the stack, or 0 otherwise in the case when stack is empty
     */
    public char top(){
        if(!stackContent.isEmpty()) {
            return stackContent.get(stackContent.size() - 1);
        }
        return 0;
    }


    /**
     * @return the size of the stack
     */
    public int size() {
        return stackContent.size();
    }

    /**
     * A method which allows for the loading of an arraylist of stack symbol elements, overwriting the currently loaded
     * stack contents
     * @param elements the stack symbols(in order) to be loaded
     */
    public void loadState(ArrayList<Character> elements) {
        stackContent = elements;

    }

    /**
     * @return The stack content. The end of the list represents the top of the stack.
     */
    public ArrayList<Character> getStackContent() {
        return stackContent;
    }

    /**
     * @return The stack content as string. The character at the end of the string represents the top of the stack.
     */
    public String getStackContentAsString() {
        if (stackContent.size() == 0) {
            return "-";
        }
        String output = "";
        for (int i = stackContent.size() - 1; i >= 0; i--) {
            output += stackContent.get(i);
        }
        return output;
    }
}
