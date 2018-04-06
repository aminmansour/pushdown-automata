package model;

import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Mod;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Test for MemoryFactory class
 */
public class ModelFactoryTest extends TestCase {


    // method create a test suite - Note 7
    public static Test suite() {
        return new TestSuite(ModelFactoryTest.class);
    }

    //load example definition
    private Definition loadDefinitionWithID(String id) {
        ArrayList<Transition> transitions = new ArrayList<>();
        ArrayList<ControlState> states = new ArrayList<>();
        ControlState q1 = new ControlState("q1");
        states.add(q1);
        ControlState q2 = new ControlState("q2");
        states.add(q2);
        ControlState q3 = new ControlState("q3");
        states.add(q3);
        Transition transition1 = new Transition(new Configuration(q1, 'g', '/'), new Action(q2, 't'));
        transitions.add(transition1);
        Transition transition2 = new Transition(new Configuration(q1, 'g', '/'), new Action(q3, 't'));
        transitions.add(transition2);
        Transition transition3 = new Transition(new Configuration(q2, 'g', '/'), new Action(q2, 't'));
        transitions.add(transition3);
        Transition transition4 = new Transition(new Configuration(q1, 'd', '/'), new Action(q1, 't'));
        transitions.add(transition4);
        return new Definition(id, states, states.get(0), transitions, true);

    }

    //clear memory of current library
    private void clearMemory() {
        try {
            Gson gson = new Gson();
            FileWriter writer = new FileWriter("library.json");
            gson.toJson(new Definition[0], writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //test the validity of testCheckForDefinitionOccurrence method
    public void testCheckForDefinitionOccurrenceMethod() {

        //clear memory
        clearMemory();

        //load definitions and save to library
        MemoryFactory.saveToLibrary(loadDefinitionWithID("id1"));
        MemoryFactory.saveToLibrary(loadDefinitionWithID("id2"));

        //test
        assertTrue(ModelFactory.checkForDefinitionOccurrence(loadDefinitionWithID("id2")));
        assertFalse(ModelFactory.checkForDefinitionOccurrence(loadDefinitionWithID("id3")));
    }

    //test the validity of loading library
    public void testStateLookupMethod() {
        //testing environment
        ArrayList<ControlState> states = new ArrayList<>();
        ControlState q1 = new ControlState("q1");
        states.add(q1);
        ControlState q2 = new ControlState("q2");
        states.add(q2);
        ControlState q3 = new ControlState("q3");
        states.add(q3);

        //does exist
        assertNotNull(ModelFactory.stateLookup(states, "q2"));
        //doesn't exist in list
        assertNull(ModelFactory.stateLookup(states, "q4"));


    }
}
