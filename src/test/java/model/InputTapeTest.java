package model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

/**
 * Test for InputTape class
 */
public class InputTapeTest extends TestCase {


    // method create a test suite - Note 7
    public static Test suite() {
        return new TestSuite(InputTapeTest.class);
    }

    // method to test the constructor of the InputTape class
    public void testConstructor() {

        // create a new InputTape
        InputTape inputTape = new InputTape();

        //expected values
        int expectedHeadPosition = -1;
        int expectedStep = 0;

        //tests
        assertEquals(expectedHeadPosition, inputTape.getHeadPosition());
        assertEquals(expectedStep, inputTape.getStep());
        assertEquals(0, inputTape.size());

    }

    //test the procedure of setting new word into tape when empty
    public void testSettingTapeWhenEmpty() {
        // create a new InputTape
        InputTape inputTape = new InputTape();

        //expected values
        char expectedSymbolAtHead = 'a';
        int expectedHeadPosition = 0;
        int expectedStep = 0;
        ArrayList<Character> desiredTapeContent = new ArrayList<>();

        desiredTapeContent.add('a');
        desiredTapeContent.add('b');
        desiredTapeContent.add('c');

        //when empty
        inputTape.setInput(desiredTapeContent);
        //check
        assertEquals(expectedSymbolAtHead, inputTape.getSymbolAtHead());
        assertEquals(expectedHeadPosition, inputTape.getHeadPosition());
        assertEquals(expectedStep, inputTape.getStep());
        assertEquals(desiredTapeContent.size(), inputTape.getRemainingInput().size());
        for (int i = 0; i < inputTape.size(); i++) {
            assertEquals(desiredTapeContent.get(i), inputTape.getRemainingInput().get(i));
        }


    }

    //test the procedure of setting new word into tape when a current input is ready loaded
    public void testSettingTapeWhenNotEmpty() {
        //when not empty
        InputTape inputTape = new InputTape();
        populateWithABCStandardInput(inputTape);


        //expected values
        char expectedSymbolAtHeadAlt = 'c';
        int expectedHeadPositionAlt = 0;
        int expectedStepAlt = 0;
        ArrayList<Character> desiredTapeContentAlt = new ArrayList<>();

        desiredTapeContentAlt.add('c');
        desiredTapeContentAlt.add('h');
        desiredTapeContentAlt.add('a');

        inputTape.setInput(desiredTapeContentAlt);
        //check
        assertEquals(expectedSymbolAtHeadAlt, inputTape.getSymbolAtHead());
        assertEquals(expectedHeadPositionAlt, inputTape.getHeadPosition());
        assertEquals(expectedStepAlt, inputTape.getStep());

        ArrayList<Character> remainingInput = inputTape.getRemainingInput();
        assertEquals(desiredTapeContentAlt.size(), remainingInput.size());
        for (int i = 0; i < inputTape.size(); i++) {
            assertEquals(desiredTapeContentAlt.get(i), remainingInput.get(i));
        }
    }

    //test the validity of the getRemainingInput() and getRemainingInputString() method
    public void testGetRemainingInputMethod() {
        // create a new InputTape
        InputTape inputTape = new InputTape();

        //test when empty
        assertTrue(inputTape.getRemainingInput().isEmpty());
        assertEquals("-", inputTape.getRemainingInputAsString());

        //test when not empty
        ArrayList<Character> content = new ArrayList<>();
        content.add('a');
        content.add('b');
        content.add('c');
        inputTape.setInput(content);

        ArrayList<Character> remainingInput = inputTape.getRemainingInput();
        assertEquals(content.size(), remainingInput.size());
        for (int i = 0; i < inputTape.size(); i++) {
            assertEquals(content.get(i), remainingInput.get(i));
        }
        assertEquals("abc", inputTape.getRemainingInputAsString());

    }

    // method to test the reading of a input symbol from the tape
    public void testReadingSymbolFromTape() {
        // create a new InputTape
        InputTape inputTape = new InputTape();

        //when empty
        assertEquals(0, inputTape.readSymbol(false));
        assertEquals(0, inputTape.getStep());
        assertEquals(-1, inputTape.getHeadPosition());

        //when not empty
        populateWithABCStandardInput(inputTape);

        assertEquals('a', inputTape.readSymbol(false));
        assertEquals(1, inputTape.getStep());
        assertEquals(1, inputTape.getHeadPosition());

        //test readSymbol for skipping symbol element
        assertEquals(0, inputTape.readSymbol(true));
        assertEquals(2, inputTape.getStep());
        assertEquals(1, inputTape.getHeadPosition());

        //test when at end of tape
        assertEquals('b', inputTape.readSymbol(false));
        assertEquals('c', inputTape.readSymbol(false));
        assertEquals(4, inputTape.getStep());
        assertEquals(3, inputTape.getHeadPosition());
    }

    //test the validity of the getSymbolAtHead() method
    public void testGetSymbolAtHead() {
        // create a new InputTape
        InputTape inputTape = new InputTape();

        //test when empty
        assertEquals(0, inputTape.getSymbolAtHead());
        assertEquals("-", inputTape.getSymbolAtHeadString());

        //test when not empty
        populateWithABCStandardInput(inputTape);

        assertEquals('a', inputTape.getSymbolAtHead());
        assertEquals("a", inputTape.getSymbolAtHeadString());

        //test when head moved by at least 1 step
        inputTape.readSymbol(false);

        assertEquals('b', inputTape.getSymbolAtHead());
        assertEquals("b", inputTape.getSymbolAtHeadString());

        //test when at end of tape
        inputTape.readSymbol(false);
        inputTape.readSymbol(false);
        assertEquals(0, inputTape.getSymbolAtHead());
        assertEquals("-", inputTape.getSymbolAtHeadString());

    }

    //test the validity of the clear() method
    public void testClearMethod() {

        // create a new InputTape
        InputTape inputTape = new InputTape();
        populateWithABCStandardInput(inputTape);
        inputTape.clear();

        //expected values
        int expectedHeadPosition = -1;
        int expectedStep = 0;

        //tests
        assertEquals(expectedHeadPosition, inputTape.getHeadPosition());
        assertEquals(expectedStep, inputTape.getStep());
        assertEquals(0, inputTape.size());
    }

    //test the validity of the isFinished() method
    public void testIsFinishedMethod() {
        // create a new InputTape
        InputTape inputTape = new InputTape();
        populateWithABCStandardInput(inputTape);

        //read tape
        assertFalse(inputTape.isFinished());
        inputTape.readSymbol(false);
        assertFalse(inputTape.isFinished());
        inputTape.readSymbol(false);
        assertFalse(inputTape.isFinished());
        inputTape.readSymbol(false);

        assertTrue(inputTape.isFinished());
    }

    //test the validity of the previous() method
    public void testPreviousMethod() {
        // create a new InputTape
        InputTape inputTape = new InputTape();
        populateWithABCStandardInput(inputTape);

        //when head is at the start of current input
        inputTape.previous();
        assertEquals(0, inputTape.getStep());
        assertEquals(0, inputTape.getHeadPosition());

        //when head is not at start
        inputTape.readSymbol(false);

        //head at 'b'
        inputTape.previous();
        assertEquals(0, inputTape.getStep());
        assertEquals(0, inputTape.getHeadPosition());

        //at end of tape
        inputTape.readSymbol(false);
        inputTape.readSymbol(false);
        inputTape.readSymbol(false);
        inputTape.previous();

        assertEquals(2, inputTape.getStep());
        assertEquals(2, inputTape.getHeadPosition());
        assertFalse(inputTape.isFinished());
    }

    //test the validity of the getOriginalWord() method
    public void testGetOriginalWordMethod() {
        // create a new InputTape
        InputTape inputTape = new InputTape();

        //when empty
        assertEquals("", inputTape.getOriginalWord());

        //when not empty
        populateWithABCStandardInput(inputTape);
        assertEquals("abc", inputTape.getOriginalWord());
    }

    //populate inputTape with a,b,c collection
    private void populateWithABCStandardInput(InputTape inputTape) {
        ArrayList<Character> content = new ArrayList<>();
        content.add('a');
        content.add('b');
        content.add('c');
        inputTape.setInput(content);
    }
}
