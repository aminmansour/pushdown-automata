package model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

/**
 * Test for ExecutionTree class
 */
public class ExecutionTreeTest extends TestCase {


    // method to test the constructor of the ExecutionTree class
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

        //create ExecutionTree
        ExecutionTree executionTree = new ExecutionTree(context);

        //test
        assertEquals(context, executionTree.getRoot());
        assertEquals(context, executionTree.getCurrent());
    }


}
