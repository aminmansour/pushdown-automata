package model;

import org.junit.Test;

public class PDAComponentTest {


    /**
     *
     * This method is performed by producing two boolean type variable.
     * A beforeMethod variable representing the existence of comment before call addToComment and a afterMethod variable representing the existence of comment after the method.
     * If beforeMethod is false and afterMethod is true, it means that the addToComments method does perform as expected.
     *
     * @throws Exception
     */
    @Test
    public void TestCorrectDeterministicTransitionIdentification() {

//        ArrayList<Transition> transitions = new ArrayList<>();
//        ArrayList<ControlState> states = new ArrayList<>();
//        states = new ArrayList<>();
//        ControlState q1 = new ControlState("q1", true);
//        states.add(q1);
//        ControlState q2 = new ControlState("q2",  true);
//        states.add(q2);
//        Transition transition1 = new Transition(new Configuration(q1, '1', 'A'), new Action(q1, 'A'));
//        transitions.add(transition1);
//        Transition transition2 = new Transition(new Configuration(q1, '1', 'A'), new Action(q2, 'A'));
//        transitions.add(transition2);
//        Transition transition3 = new Transition(new Configuration(q2, '1', 'A'), new Action(q1, 'A'));
//        transitions.add(transition3);
//        Transition transition4 = new Transition(new Configuration(q1, null, 'A'), new Action(q1, 'A'));
//        transitions.add(transition4);
//        Definition def = new Definition("1", null, states, false, transitions);
//        Set<Transition> computatedSet = def.getDeterministicTransitions();
//
//        Set<Transition> correctList = new HashSet<>();
//        correctList.add(transition1);
//        correctList.add(transition2);
//        correctList.add(transition4);
//
//        assertEquals(def.isDeterministic(),true);
//        assertEquals(true,correctList.containsAll(computatedSet));

    }

}