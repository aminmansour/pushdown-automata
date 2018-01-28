package model;

import java.util.ArrayList;
import java.util.List;

public class InputTape {
    private int headIndex;
    private ArrayList<Character> input;
    private int step;


    public InputTape() { clear(); }

    public char readSymbol() {
        if (headIndex < input.size()) {
            char toReturn = input.get(headIndex);
            headIndex++;
            step++;
            return toReturn;
        }
        return 0;
    }

    public void previous() {
        if (headIndex > 0){
            headIndex--;
            step--;
        }
    }

    public boolean allSymbolsRead(){
        if(headIndex==(input.size()-1)){
            return true;
        }
        return false;
    }

    public String getStringAtHead(){
        return (headIndex>=input.size())?"none":input.get(headIndex)+"";
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
        step = 0;
        this.input = new ArrayList<>();
    }


    public void setHeadIndex(int headIndex) {
        this.headIndex = headIndex;
    }

    public void setStep(int step) { this.step = step; }

    public int getStep() { return step; }

    public Character getSymbolAt(int index) {
        return input.get(index);
    }

    public boolean hasFinished(){
        return headIndex==input.size()-1;
    }

    public List<Character> getRemainingInput() {
        return input.subList(headIndex,input.size());
    }
}
