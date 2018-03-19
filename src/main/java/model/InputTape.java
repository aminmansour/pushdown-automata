package model;

import java.util.ArrayList;

/**
 * Input tape represent the theoretical input tape found in a PDA. Its wrapped around an arraylist
 */
public class InputTape {

    //components
    private int headIndex;
    private ArrayList<Character> input;
    private int step;


    /**
     * A basic InputTape constructor
     */
    public InputTape() {
        clear();
    }

    /**
     * A method which reads the current symbol depending on the head position. Then it
     * increments the current head index with the step counter.
     *
     * @param skipSymbol identifies whether the input symbol of transition is a skipping one
     * @return the symbol character read
     */
    public char readSymbol(boolean skipSymbol) {
        if (!skipSymbol && input.size() > 0 && headIndex < input.size()) {
            char toReturn = input.get(headIndex);
            headIndex++;
            System.out.println(headIndex + " a");
            step++;
            return toReturn;
        }
        step++;
        return 0;
    }

    /**
     * A method which takes the tape's head one step back
     */
    public void previous() {
        if (headIndex > 0){
            headIndex--;
            step--;
        }
    }

    /**
     * A method which gets the symbol sting at head without reading it
     *
     * @return the symbol at head, or 'none' otherwise when the tape is empty
     */
    public String getStringSymbolAtHead() {
        return (headIndex >= input.size() || headIndex == -1) ? "-" : input.get(headIndex) + "";
    }

    /**
     * A method which gets the symbol character at head without reading it
     *
     * @return the symbol at head, or '-' otherwise when the tape is empty
     */
    public char getSymbolAtHead() {
        return (headIndex >= input.size() || headIndex == -1) ? '-' : input.get(headIndex);
    }

    public void setInput(ArrayList<Character> input) {
        headIndex = 0;
        this.input = input;
    }

    /**
     * A method which resets the input tape and clears the previous input
     */
    public void clear() {
        headIndex = -1;
        step = 0;
        this.input = new ArrayList<>();
    }


    public void setHeadIndex(int headIndex) {
        this.headIndex = headIndex;
    }

    public void setStep(int step) { this.step = step; }

    public int getStep() {
        return step;
    }

    public boolean isFinished() {
        return headIndex == input.size();
    }

    /**
     * A method which gets the reamining input in tape and
     * returns it
     *
     * @return the remaining input of tape in the form of an array list of characters
     */
    public ArrayList<Character> getRemainingInput() {
        ArrayList<Character> remainingInput = new ArrayList<>();
        if (headIndex >= 0) {
            for (int i = headIndex; i < input.size(); i++) {
                remainingInput.add(input.get(i));
            }
        }
        return remainingInput;
    }


    /**
     * A method which gets the remaining input in tape and
     * returns it
     * @return the remaining input of tape in the form of an String
     */
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

    /**
     * @return the size of the initial string that was inputted
     */
    public int getSize() {
        return input == null ? 0 : input.size();
    }

    /**
     * @return The original word that was loaded into tape
     */
    public String getOriginalWord() {
        String originalWord = "";
        for (Character symbol : input) {
            originalWord += symbol;
        }
        return originalWord;
    }

    public int getHeadPosition() {
        return headIndex;
    }

    /**
     * A method which determines whether step is the last step of computation
     *
     * @return true, if last step, else false otherwise
     */
    public boolean isLastStep() {
        return step == getOriginalWord().length();
    }

}
