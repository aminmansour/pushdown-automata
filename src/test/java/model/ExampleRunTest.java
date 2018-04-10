package model;

import controller.PDARunnerController;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

/**
 * The ExamplesRunTest validates the running of several PDA examples. 5 vastly different examples are found in the
 * application and each can be loaded by the user. It is impossible to guarantee that a PDA correctly recognizes a
 * language without examining all possible inputs.
 * For each, three inputs which the PDA should and shouldn't recognize are tested.
 */
public class ExampleRunTest extends TestCase {


    // method create a test suite - Note 7
    public static Test suite() {
        return new TestSuite(ExampleRunTest.class);
    }

    /**
     * A method testing whether the PDA machine  accepts
     * words in the language of matched opening and closing parenthesis
     */
    public void testPDAExample1() {

        Definition definition = MemoryFactory.loadExamples().get(4);
        PDAMachine machine = new PDAMachine(definition);

        //words it should accepts
        machine.loadInput("((()))");
        int result = machine.runInstantRunDFS(false);
        assertTrue(machine.isAccepted());

        machine.loadInput("");
        machine.runInstantRunDFS(false);
        assertTrue(machine.isAccepted());


        machine.loadInput("()");
        machine.runInstantRunDFS(false);
        assertTrue(machine.isAccepted());

        //words it shouldn't

        machine.loadInput("((())");
        machine.runInstantRunDFS(false);
        assertFalse(machine.isAccepted());

        machine.loadInput("(((");
        machine.runInstantRunDFS(false);
        assertFalse(machine.isAccepted());

        machine.loadInput("(");
        machine.runInstantRunDFS(false);
        assertFalse(machine.isAccepted());

    }


    /**
     * A method testing whether the PDA machine  accepts
     * words in the language { 0^n 1^n | n >= 0 }.
     */
    public void testPDAExample2() {

        Definition definition = MemoryFactory.loadExamples().get(0);
        PDAMachine machine = new PDAMachine(definition);

        //words it should accepts
        machine.loadInput("000111");
        machine.runInstantRunDFS(false);
        assertTrue(machine.isAccepted());

        machine.loadInput("");
        machine.runInstantRunDFS(false);
        assertTrue(machine.isAccepted());

        machine.loadInput("01");
        machine.runInstantRunDFS(false);
        assertTrue(machine.isAccepted());
        //words it shouldn't

        machine.loadInput("10");
        //3 is return code for no solution found
        machine.runInstantRunDFS(false);
        assertFalse(machine.isAccepted());

        machine.loadInput("1");
        machine.runInstantRunDFS(false);
        assertFalse(machine.isAccepted());

        machine.loadInput("0001111");
        machine.runInstantRunDFS(false);
        assertFalse(machine.isAccepted());

    }

    /**
     * A method testing whether the PDA machine  accepts
     * words in the language of even-lengthened palindromes
     * (words that are the same forward and backward).
     */
    public void testPDAExample3() {

        Definition definition = MemoryFactory.loadExamples().get(1);
        PDAMachine machine = new PDAMachine(definition);

        //words it should accepts
        machine.loadInput("101101");
        machine.runInstantRunDFS(false);
        assertTrue(machine.isAccepted());

        machine.loadInput("11");
        machine.runInstantRunDFS(false);
        assertTrue(machine.isAccepted());


        machine.loadInput("1001");
        machine.runInstantRunDFS(false);
        assertTrue(machine.isAccepted());

        //words it shouldn't

        machine.loadInput("101");
        machine.runInstantRunDFS(false);
        assertFalse(machine.isAccepted());

        machine.loadInput("1110");
        machine.runInstantRunDFS(false);
        assertFalse(machine.isAccepted());

        machine.loadInput("0001111");
        machine.runInstantRunDFS(false);
        assertFalse(machine.isAccepted());

    }

    /**
     * A method testing whether the PDA machine  accepts
     * words in the language  { 0^n 1^2n | n >= 0 }
     */
    public void testPDAExample4() {

        Definition definition = MemoryFactory.loadExamples().get(3);
        PDAMachine machine = new PDAMachine(definition);

        //words it should accepts
        machine.loadInput("011");
        machine.runInstantRunDFS(false);
        assertTrue(machine.isAccepted());

        machine.loadInput("000111111");
        machine.runInstantRunDFS(false);
        assertTrue(machine.isAccepted());


        machine.loadInput("000001111111111");
        machine.runInstantRunDFS(false);
        assertTrue(machine.isAccepted());

        //words it shouldn't

        machine.loadInput(" ");
        machine.runInstantRunDFS(false);
        assertFalse(machine.isAccepted());

        machine.loadInput("110");
        machine.runInstantRunDFS(false);
        assertFalse(machine.isAccepted());

        machine.loadInput("000111");
        machine.runInstantRunDFS(false);
        assertFalse(machine.isAccepted());

    }

    /**
     * A method testing whether the PDA machine  accepts
     * words in the language {a^i b^j c^k | i , j , k >= 0, and i = j  or i = k }.
     */
    public void testPDAExample5() {

        Definition definition = MemoryFactory.loadExamples().get(2);
        PDAMachine machine = new PDAMachine(definition);

        //words it should accepts
        machine.loadInput("aabcc");
        machine.runInstantRunDFS(false);
        assertTrue(machine.isAccepted());

        machine.loadInput("aaabbb");
        machine.runInstantRunDFS(false);
        assertTrue(machine.isAccepted());


        machine.loadInput("abc");
        machine.runInstantRunDFS(false);
        assertTrue(machine.isAccepted());

        //words it shouldn't

        machine.loadInput("abbcc");
        machine.runInstantRunDFS(false);
        assertFalse(machine.isAccepted());

        machine.loadInput("a");
        machine.runInstantRunDFS(false);
        assertFalse(machine.isAccepted());

        machine.loadInput("ababc");
        machine.runInstantRunDFS(false);
        assertFalse(machine.isAccepted());

    }

}
