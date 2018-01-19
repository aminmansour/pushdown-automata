package model;

import java.util.ArrayList;

public class InputTape {
    private int headIndex;
    private ArrayList<Character> input;


    public InputTape() { clear(); }

    public char readSymbol() {
        char toReturn = input.get(headIndex);
        headIndex++;
        return toReturn;
    }

    public char getSymbolAtHead(){
        return input.get(headIndex);
    }

    public void setInput(ArrayList<Character> input) {
        headIndex = 0;
        this.input = input;
    }

    public void clear(){
        headIndex = -1;
        this.input = new ArrayList<>();
    }


    public void setHeadIndex(int headIndex) {
        this.headIndex = headIndex;
    }

    public Character getSymbolAt(int index) {
        return input.get(index);
    }

    public boolean hasFinished(){
        return headIndex==input.size()-1;
    }
}
