package model;

import java.util.ArrayList;

public class InputTape {
    private int headIndex;
    private ArrayList<Character> input;
    private int step;


    public InputTape() { clear(); }

    public char readSymbol() {
        if (input.size() > 0 && headIndex < input.size()) {
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
        return headIndex == (input.size() - 1);
    }

    public String getStringAtHead(){
        return (headIndex>=input.size())?"none":input.get(headIndex)+"";
    }

    public char getSymbolAtHead(){
        return (headIndex >= input.size()) ? '-' : input.get(headIndex);
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

    public ArrayList<Character> getRemainingInput() {
        ArrayList<Character> remainingInput = new ArrayList<>(input.size() - headIndex);
        for (int i = headIndex; i < input.size(); i++) {
            remainingInput.add(input.get(i));
        }
        return remainingInput;
    }


    public String getRemainingInputAsString() {
        if ((headIndex >= input.size())) {
            return "-";
        }
        String output = "";
        for (Character sym : input.subList(headIndex, input.size())) {
            output += sym;
        }
        return output;
    }

    public int getSize() {
        return input.size();
    }

    public String getOriginalWord() {
        String originalWord = "";
        for (Character symbol : input) {
            originalWord += symbol;
        }
        return originalWord;
    }
}
