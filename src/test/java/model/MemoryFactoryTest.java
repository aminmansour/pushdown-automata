package model;

import com.google.gson.Gson;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Test for MemoryFactory class
 */
public class MemoryFactoryTest extends TestCase {


    // method create a test suite - Note 7
    public static Test suite() {
        return new TestSuite(MemoryFactoryTest.class);
    }

    //load example definition
    private Definition loadStandardDefinition() {
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
        return new Definition("pda-test", states, states.get(0), transitions, true);

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

    //test the validity of loading library
    public void testSaveToLibraryMethod() {

        //clear memory
        clearMemory();

        //load standard definition and save to library
        Definition definitionToSave = loadStandardDefinition();
        MemoryFactory.saveToLibrary(definitionToSave);


        //library in memory
        ArrayList<Definition> library = new ArrayList<>();
        Gson gson = new Gson();
        Reader reader = null;
        try {
            reader = new FileReader("library.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        library.addAll(Arrays.asList(gson.fromJson(reader, Definition[].class)));

        //test
        assertEquals(1, library.size());
        assertEquals(definitionToSave.getIdentifier(), library.get(0).getIdentifier());
    }

    //test the validity of loading library
    public void testLoadingLibrary() {
        //clear memory
        clearMemory();

        //when empty
        ArrayList<Definition> lib = MemoryFactory.loadLibrary();

        //test
        assertEquals(0, lib.size());

        //when not empty
        Definition definitionToSave = loadStandardDefinition();
        MemoryFactory.saveToLibrary(definitionToSave);
        ArrayList<Definition> libA = MemoryFactory.loadLibrary();

        //test
        assertEquals(1, libA.size());
        assertEquals(definitionToSave.getIdentifier(), libA.get(0).getIdentifier());

    }
//    }
//    public void testValidityOfExamples(){
//        ArrayList<Definition> examples = MemoryFactory.loadExamples();
//
//        for(Definition definition : examples){
//            assertTrue(definition.getStates().size()>1);
//            assertTrue(definition.getStates().size()>1);
//        }
//    }

    //test the validity of loading example library
    public void testLoadingExamples() {
        ArrayList<Definition> examples = MemoryFactory.loadExamples();
        assertEquals(5, examples.size());

    }

    //test the validity of saveState Method
    public void testSaveStateMethod() {
        //clear memory
        clearMemory();

        //load standard definition and save to library
        Definition definitionToSave = loadStandardDefinition();
        MemoryFactory.saveToLibrary(definitionToSave);

        ArrayList<Definition> library = MemoryFactory.loadExamples();

        String newIdentifer = "CHANGED";
        Definition defToModify = library.get(0);
        defToModify.setIdentifier(newIdentifer);

        //save
        MemoryFactory.saveState();

        //test
        ArrayList<Definition> newLibrary = MemoryFactory.loadExamples();
        Definition defToTest = library.get(0);

        assertEquals(newIdentifer, defToTest.getIdentifier());
    }
}
