package model;

import java.util.ArrayList;

public class PushDownStack {
    private ArrayList<Character> stackContent;


    public PushDownStack() {
        this.stackContent = new ArrayList<>();
        stackContent.add(null);
    }

    public char pop(){
        if(!stackContent.isEmpty()) {
            return stackContent.remove(stackContent.size()-1);
        }
        return 0;
    }

    public void push(char symbol){
        stackContent.add(symbol);
    }

    public boolean isEmpty() {
        return stackContent.size()==1;
    }

    public void clear(){
        stackContent.clear();
    }

    public char top(){
        if(!stackContent.isEmpty()) {
            return stackContent.get(stackContent.size() - 1);
        }
        return 0;
    }

    public int size() {
        return stackContent.size();
    }

}
