package model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

/**
 * Test for PushDownStackTest
 */
public class PushDownStackTest extends TestCase {


    // method create a test suite - Note 7
    public static Test suite() {
        return new TestSuite(PushDownStackTest.class);
    }

    // method to test the constructor of the PushDownStack class
    public void testConstructor() {

        // create a new stack
        PushDownStack stack = new PushDownStack();

        // verify that the object is constructed properly - Note 5
        assertNotNull(stack.getStackContent());
        assertEquals(0, stack.getStackContent().size());
    }

    // method to test pushing element
    public void testPushingElement() {
        // create a new stack
        PushDownStack stack = new PushDownStack();
        //when empty
        ArrayList<Character> expectedStack = new ArrayList<>();
        expectedStack.add('a');

        stack.push('a');

        assertEquals(expectedStack.size(), stack.size());
        for (int i = 0; i < stack.size(); i++) {
            assertEquals(expectedStack.get(i), stack.getStackContent().get(i));
        }

        //when not empty
        expectedStack.add('c');

        stack.push('c');
        assertEquals(expectedStack.size(), stack.size());
        for (int i = 0; i < stack.size(); i++) {
            assertEquals(expectedStack.get(i), stack.getStackContent().get(i));
        }

    }

    // method to test the retrieval of top values from stack
    public void testTop() {
        // create a new stack
        PushDownStack stack = new PushDownStack();
        //when empty
        //check method returns correct output for empty stack
        assertEquals(0, stack.top());
        //check stack size is still 0
        assertEquals(0, stack.getStackContent().size());


        //when not empty
        stack.push('a');
        stack.push('b');
        stack.push('c');

        //check correct top of stack value is retrieved
        assertEquals('c', stack.top());

        //check  for if item removed
        assertEquals(3, stack.size());

        //check if top of stack still has correct value
        Character top = stack.getStackContent().get(stack.size() - 1);
        assertEquals('c', top.charValue());
    }

    // method to test the popping method from stack
    public void testPop() {
        // create a new stack
        PushDownStack stack = new PushDownStack();
        //when empty
        //check method returns correct output for empty stack
        assertEquals(0, stack.pop());
        //check stack size is still 0
        assertEquals(0, stack.getStackContent().size());


        //when not empty
        stack.push('a');
        stack.push('b');
        stack.push('c');

        //check value popped
        assertEquals('c', stack.pop());

        //check if item removed
        assertEquals(2, stack.size());

        //check if new top is correct value
        Character newTop = stack.top();
        assertEquals('b', newTop.charValue());
    }

    public void testIsEmptyAndClearMethod() {
        PushDownStack stack = new PushDownStack();

        //empty
        //when empty
        assertEquals(true, stack.isEmpty());

        //when not empty
        stack.push('a');
        assertEquals(false, stack.isEmpty());


        //clear
        stack.clear();
        assertEquals(true, stack.isEmpty());
    }

    public void testLoadState() {
        ArrayList<Character> stackContentToLoad = new ArrayList<>();
        stackContentToLoad.add('a');
        stackContentToLoad.add('b');
        stackContentToLoad.add('c');

        PushDownStack stack = new PushDownStack();
        stack.loadState(stackContentToLoad);

        assertEquals(stackContentToLoad.size(), stack.size());
        for (int i = 0; i < stack.size(); i++) {
            assertEquals(stackContentToLoad.get(i), stack.getStackContent().get(i));
        }

    }

    public void testGetStackContentAsStringMethod() {
        PushDownStack stack = new PushDownStack();

        //when empty
        assertEquals("-", stack.getStackContentAsString());

        //when not empty
        stack.push('a');
        stack.push('b');
        stack.push('c');
        assertEquals("cba", stack.getStackContentAsString());
    }
}
