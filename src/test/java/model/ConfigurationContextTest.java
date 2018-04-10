package model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

/**
 * Test for ConfigurationContext class
 */
public class ConfigurationContextTest extends TestCase {



    // method create a test suite - Note 7
    public static Test suite() {
        return new TestSuite(ConfigurationContextTest.class);
    }

    // method to test the constructor of the ConfigurationContext class
    public void testConstructor() {

        //create a set of states
        ArrayList<ControlState> states = new ArrayList<>();
        ControlState q1 = new ControlState("q1");
        q1.markAsInitial();
        states.add(q1);
        ControlState q2 = new ControlState("q2");
        states.add(q2);

        // create a root ConfigurationContext
        ConfigurationContext context = new ConfigurationContext(q1, null, new ArrayList<>(), 0, 0, 0);


        //test using getter fields
        assertEquals(0, context.getStep());
        assertEquals(0, context.getHeadPosition());
        assertEquals(0, context.getTotalSiblings());
        assertEquals(1, context.getNumberOfVisits());
        assertEquals(0, context.getExploredChildren().size());
        assertEquals(q1, context.getState());
        assertEquals(0, context.getStackState().size());
        assertNull(context.getParent());

        //create an arbitrary ConfigurationContext stemming from root

        //dummy stack
        PushDownStack stack = new PushDownStack();
        stack.push('a');
        stack.push('b');

        //dummy tape
        InputTape inputTape = new InputTape();
        ArrayList<Character> input = new ArrayList<>();
        input.add('a');
        input.add('b');
        input.add('c');
        inputTape.setInput(input);
        inputTape.readSymbol(false);

        ConfigurationContext contextOther = new ConfigurationContext(q2, context, stack.getStackContent(), inputTape.getHeadPosition(), inputTape.getStep(), 1);

        //test using getter fields
        assertEquals(1, contextOther.getStep());
        assertEquals(1, contextOther.getHeadPosition());
        assertEquals(1, contextOther.getTotalSiblings());
        assertEquals(1, contextOther.getNumberOfVisits());
        assertEquals(0, contextOther.getExploredChildren().size());
        assertEquals(q2, contextOther.getState());
        assertEquals(2, contextOther.getStackState().size());
        assertEquals(q1, contextOther.getParent().getState());

    }

    //test the validity of addChildMethod()
    public void testAddChildMethod() {

        //create a set of states
        ArrayList<ControlState> states = new ArrayList<>();
        ControlState q1 = new ControlState("q1");
        q1.markAsInitial();
        states.add(q1);
        ControlState q2 = new ControlState("q2");
        states.add(q2);

        // create a root ConfigurationContext
        ConfigurationContext context = new ConfigurationContext(q1, null, new ArrayList<>(), 0, 0, 0);
        ConfigurationContext contextOther = new ConfigurationContext(q2, context, new ArrayList<>(), 0, 0, 1);

        context.addChild(contextOther);

        //test
        assertEquals(contextOther, context.getExploredChildren().get(0));

    }

    //test the validity of the hasChild() method
    public void testHasChildWithContext() {

        //create a set of states
        ArrayList<ControlState> states = new ArrayList<>();
        ControlState q1 = new ControlState("q1");
        q1.markAsInitial();
        states.add(q1);
        ControlState q2 = new ControlState("q2");
        states.add(q2);

        // create a root ConfigurationContext
        ConfigurationContext contextRoot = new ConfigurationContext(q1, null, new ArrayList<>(), 0, 0, 0);

        //case where child exists
        //create child with some stack state
        ArrayList<Character> stackState = new ArrayList<>();
        stackState.add('a');
        ConfigurationContext context2 = new ConfigurationContext(q2, contextRoot, stackState, 2, 1, 1);
        contextRoot.addChild(context2);

        //look for child of root
        ArrayList<Character> stackStateToLookFor = new ArrayList<>();
        stackStateToLookFor.add('a');
        assertNotNull(contextRoot.hasChild(q2, stackStateToLookFor, 2));

        //case where child doesn't exist
        stackState.add('b');
        assertNull(contextRoot.hasChild(q2, stackStateToLookFor, 1));
    }

    //test the validity of the getNextChildInPath() method
    public void testGetNextChildInPath() {
        //create a set of states
        ArrayList<ControlState> states = new ArrayList<>();
        ControlState q1 = new ControlState("q1");
        q1.markAsInitial();
        states.add(q1);
        ControlState q2 = new ControlState("q2");
        states.add(q2);

        // create a root ConfigurationContext and mark in path
        ConfigurationContext contextRoot = new ConfigurationContext(q1, null, new ArrayList<>(), 0, 0, 0);
        contextRoot.markInPath(true);

        //case where child in path exists
        //create child with some stack state
        ConfigurationContext contextOther = new ConfigurationContext(q2, contextRoot, new ArrayList<>(), 2, 1, 1);
        contextOther.markInPath(true);
        contextRoot.addChild(contextOther);

        //test
        assertEquals(contextOther, contextRoot.getNextChildInPath());

        //case where child in path doesn't exists
        assertNull(contextOther.getNextChildInPath());

    }

    //test the validity of the getStackStateInStringFormat() method
    public void testGetStackStateInStringFormat() {
        //create a set of states
        ArrayList<ControlState> states = new ArrayList<>();
        ControlState q1 = new ControlState("q1");
        q1.markAsInitial();
        states.add(q1);


        // create a root ConfigurationContext
        ArrayList<Character> stackState = new ArrayList<>();
        ConfigurationContext contextRoot = new ConfigurationContext(q1, null, stackState, 0, 0, 0);

        //when empty
        assertEquals("-", contextRoot.getStackStateInStringFormat());

        //when not empty
        stackState.add('a');
        stackState.add('b');
        stackState.add('c');
        assertEquals("cba", contextRoot.getStackStateInStringFormat());
    }
}
