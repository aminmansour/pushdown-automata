package model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Test for PDAMachine test
 */
public class PDAMachineTest extends TestCase {


    // method create a test suite - Note 7
    public static Test suite() {
        return new TestSuite(PDAMachineTest.class);
    }

    //load example definition
    private Definition loadDefinitionWithID(String id) {
        ArrayList<Transition> transitions = new ArrayList<>();
        ArrayList<ControlState> states = new ArrayList<>();
        ControlState q1 = new ControlState("q1");
        q1.markAsInitial();
        states.add(q1);
        ControlState q2 = new ControlState("q2");
        states.add(q2);
        ControlState q3 = new ControlState("q3");
        states.add(q3);
        ControlState q4 = new ControlState("q4");
        q4.markAsAccepting();
        states.add(q4);
        Transition transition1 = new Transition(new Configuration(q1, 'g', '/'), new Action(q3, 't'));
        transitions.add(transition1);
        Transition transition2 = new Transition(new Configuration(q1, 'g', '/'), new Action(q4, 't'));
        transitions.add(transition2);
        Transition transition3 = new Transition(new Configuration(q1, 'g', '/'), new Action(q2, '/'));
        transitions.add(transition3);
        Transition transition4 = new Transition(new Configuration(q3, 'd', 't'), new Action(q4, '/'));
        transitions.add(transition4);
        return new Definition(id, states, states.get(0), transitions, true);

    }

    // method to test the constructor of the PDAMachine class
    public void testConstructor() {

        // create a new pdaMachine
        PDAMachine pda = new PDAMachine(loadDefinitionWithID("test-pda"));

        assertNotNull(pda.getDefinition());
        assertNotNull(pda.getTape());
        assertEquals(0, pda.getTape().size());
        assertNotNull(pda.getStack());
        assertEquals(0, pda.getStack().size());
        assertNotNull(pda.getDefinition());

    }

    // method to test non-deterministic detection
    public void testGetNonDeterministicTransitions() {
        // create a new pdaMachine
        PDAMachine pda = new PDAMachine(loadDefinitionWithID("test-pda"));

        //test
        ArrayList<Transition> transitions = pda.getDefinition().getTransitions();
        Set<Transition> correctNonDeterministicTransitions = new HashSet<>();
        correctNonDeterministicTransitions.add(transitions.get(0));
        correctNonDeterministicTransitions.add(transitions.get(1));
        correctNonDeterministicTransitions.add(transitions.get(2));

        Set<Transition> nonDeterministicSet = pda.getNonDeterministicTransitions();

        assertEquals(correctNonDeterministicTransitions, nonDeterministicSet);


        ArrayList<Definition> lib = MemoryFactory.loadLibrary();
        PDAMachine dPda = new PDAMachine(lib.get(0));
        correctNonDeterministicTransitions.clear();
        ArrayList<Transition> transitions1 = dPda.getDefinition().getTransitions();
        correctNonDeterministicTransitions.add(transitions1.get(0));
        correctNonDeterministicTransitions.add(transitions1.get(1));


        //test
        Set<Transition> nonDeterministicSetAlt = dPda.getNonDeterministicTransitions();
        assertEquals(correctNonDeterministicTransitions, nonDeterministicSetAlt);

    }

    // method to test the validity of getPossibleTransitions() method
    public void testGetPossibleTransitions() {

        PDAMachine pda = new PDAMachine(loadDefinitionWithID("test-pda"));
        pda.loadInput("g");
        ArrayList<Transition> transitions = pda.getDefinition().getTransitions();

        ArrayList<Transition> correctTransitionsFromQ1 = new ArrayList<>();
        correctTransitionsFromQ1.add(transitions.get(0));
        correctTransitionsFromQ1.add(transitions.get(1));
        correctTransitionsFromQ1.add(transitions.get(2));

        //test
        ArrayList<Transition> actualTransitionsFromQ1 = pda.getPossibleTransitionsFromCurrent();

        assertEquals(correctTransitionsFromQ1.size(), actualTransitionsFromQ1.size());
        for (int i = 0; i < correctTransitionsFromQ1.size(); i++) {
            assertEquals(correctTransitionsFromQ1.get(i), actualTransitionsFromQ1.get(i));
        }

    }

    // method to test the validity of executeTransition() method
    public void testExecuteTransitionMethod() {
        PDAMachine pda = new PDAMachine(loadDefinitionWithID("test-pda"));
        pda.loadInput("gd");

        Transition transition = pda.getPossibleTransitionsFromCurrent().get(0);
        pda.executeTransition(transition, 2);

        assertEquals('t', pda.getStack().top());
        assertEquals(1, pda.getStack().size());
        assertEquals('d', pda.getTape().getSymbolAtHead());
        assertEquals(transition.getAction().getNewState(), pda.getCurrentState());
    }

    // method to test the validity of addTransition() method
    public void testAddTransitionMethod() {
        PDAMachine pda = new PDAMachine(loadDefinitionWithID("test-pda"));
        //4 transitions before
        Transition transitionToAdd = new Transition(new Configuration(pda.getDefinition().getStates().get(0), 'c', '/'), new Action(pda.getDefinition().getStates().get(1), 't'));
        pda.addTransition(transitionToAdd);

        assertEquals(5, pda.getDefinition().getTransitions().size());

    }

    // method to test the validity of loadInput() method
    public void testLoadInputMethod() {
        //set up environment
        PDAMachine pda = new PDAMachine(loadDefinitionWithID("test-pda"));
        pda.loadInput("gd");

        ArrayList<Character> correctTapeElements = new ArrayList<>();
        //test
        correctTapeElements.add('g');
        correctTapeElements.add('d');
        assertEquals(pda.getDefinition().getInitialState(), pda.getCurrentState());
        assertEquals(correctTapeElements.size(), pda.getTape().getRemainingInput().size());
        for (int i = 0; i < correctTapeElements.size(); i++) {
            assertEquals(correctTapeElements.get(i), pda.getTape().getRemainingInput().get(i));
        }
        assertEquals(pda.getDefinition().getInitialState(), pda.getExecutionTree().getRoot().getState());

    }

    // method to test the validity of loadConfigurationContext() method
    public void testLoadConfigurationContextMethod() {
        PDAMachine pda = new PDAMachine(loadDefinitionWithID("test-pda"));
        pda.loadInput("gd");

        //create a set of states
        ArrayList<ControlState> states = new ArrayList<>();
        ControlState q1 = new ControlState("q1");
        q1.markAsInitial();
        states.add(q1);
        ControlState q2 = new ControlState("q2");
        states.add(q2);

        // create a root ConfigurationContext
        ArrayList<Character> stackState = new ArrayList<>();
        stackState.add('a');
        ConfigurationContext context = new ConfigurationContext(q1, null, stackState, 1, 1, 0);

        //test
        pda.loadConfigurationContext(context);
        assertEquals(q1, pda.getCurrentState());
        assertEquals(1, pda.getTape().getHeadPosition());
        assertEquals(1, pda.getTape().getStep());
        assertEquals(stackState, pda.getStack().getStackContent());
        assertEquals(context, pda.getExecutionTree().getCurrent());
    }

    //test the validity of the previous() method
    public void testPreviousMethod() {
        //set up environment
        PDAMachine pda = new PDAMachine(loadDefinitionWithID("test-pda"));
        pda.loadInput("gd");

        //when head is at the start of current input
        pda.previous();
        assertEquals(0, pda.getTape().getStep());
        assertEquals(0, pda.getTape().getHeadPosition());

        //when head is not at start
        pda.executeTransition(pda.getPossibleTransitionsFromCurrent().get(0), 2);
        //pda at q2, head = 1, step = 1,stack top symbol : t

        pda.previous();
        assertEquals(0, pda.getTape().getStep());
        assertEquals(0, pda.getTape().getHeadPosition());
        assertEquals(pda.getDefinition().getInitialState(), pda.getCurrentState());
        assertTrue(pda.getStack().getStackContent().isEmpty());
        assertEquals(pda.getDefinition().getInitialState(), pda.getExecutionTree().getCurrent().getState());

    }

    // method to test the validity of redo() method
    public void testRedoMethod() {
        PDAMachine pda = new PDAMachine(loadDefinitionWithID("test-pda"));

        //when empty
        //test
        pda.redo();

        assertNull(pda.getCurrentState());
        assertTrue(pda.getTape().getStep() == 0);
        assertTrue(pda.getTape().getHeadPosition() == 0);
        assertTrue(pda.getStack().getStackContent().isEmpty());


        //set up environment
        //pda at q2, head = 1, step = 1,stack top symbol : t
        pda.loadInput("g");
        pda.executeTransition(pda.getPossibleTransitionsFromCurrent().get(0), 2);

        //test
        pda.redo();

        assertEquals(pda.getDefinition().getInitialState(), pda.getCurrentState());
        assertTrue(pda.getTape().getStep() == 0);
        assertTrue(pda.getTape().getHeadPosition() == 0);
        assertTrue(pda.getStack().getStackContent().isEmpty());

    }

    // method to test the validity of stop() method
    public void testStopMethod() {
        //set up environment
        //pda at q2, head = 1, step = 1,stack top symbol : t
        PDAMachine pda = new PDAMachine(loadDefinitionWithID("test-pda"));

        //test
        pda.stop();

        assertNull(pda.getCurrentState());
        assertTrue(pda.getTape().getRemainingInput().isEmpty());
        assertTrue(pda.getStack().getStackContent().isEmpty());

        pda.loadInput("g");
        pda.executeTransition(pda.getPossibleTransitionsFromCurrent().get(0), 2);

        //test
        pda.stop();
        assertNull(pda.getCurrentState());
        assertTrue(pda.getTape().getRemainingInput().isEmpty());
        assertTrue(pda.getStack().getStackContent().isEmpty());
    }

    // method to test the validity of isAccepted() method for an accept-by-accepting-state PDA machine
    public void testIsAcceptedMethodWhenAcceptByFAcceptingState() {
        //case when accepted
        PDAMachine pda = new PDAMachine(loadDefinitionWithID("test-pda"));
        pda.loadInput("gd");
        pda.executeTransition(pda.getPossibleTransitionsFromCurrent().get(0), pda.getPossibleTransitionsFromCurrent().size());
        assertFalse(pda.isAccepted());
        pda.executeTransition(pda.getPossibleTransitionsFromCurrent().get(0), pda.getPossibleTransitionsFromCurrent().size());
        assertTrue(pda.isAccepted());

        //case when not accepted on accepting state
        PDAMachine pda2 = new PDAMachine(loadDefinitionWithID("test-pda"));
        pda2.loadInput("gd");
        pda2.executeTransition(pda2.getPossibleTransitionsFromCurrent().get(1), pda2.getPossibleTransitionsFromCurrent().size());
        assertFalse(pda2.isAccepted());
    }

    // method to test the validity of isAccepted() method for an accept-by-empty-stack PDA machine
    public void testIsAcceptedMethodWhenEmptyStack() {
        //case when accepted
        PDAMachine pda = new PDAMachine(loadDefinitionWithID("test-pda"));
        pda.getDefinition().setAcceptByFinalState(false);
        pda.loadInput("gd");

        pda.executeTransition(pda.getPossibleTransitionsFromCurrent().get(0), pda.getPossibleTransitionsFromCurrent().size());
        assertFalse(pda.isAccepted());
        pda.executeTransition(pda.getPossibleTransitionsFromCurrent().get(0), pda.getPossibleTransitionsFromCurrent().size());
        assertTrue(pda.isAccepted());

        //case when not accepted on empty stack
        PDAMachine pda2 = new PDAMachine(loadDefinitionWithID("test-pda"));
        pda2.loadInput("gd");
        pda2.executeTransition(pda2.getPossibleTransitionsFromCurrent().get(2), pda2.getPossibleTransitionsFromCurrent().size());
        assertFalse(pda2.isAccepted());
    }
}
