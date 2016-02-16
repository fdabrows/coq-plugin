package org.univorleans.coq.coqtop;

import org.junit.Test;

/**
 * Created by dabrowski on 29/01/2016.
 */
public class StackTest {

    @Test
    public void testSave() throws Exception {
/*
        CoqtopPrompt prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 1 || 0 < </prompt>");

        State state0 = new State(prompt, 0);
        Stack stack = new Stack(state0);

        assertTrue(stack.top().equals(state0));

        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 2 || 1 < </prompt>");
        State state = new State(prompt, 0);
        stack.save(state);
        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 3 || 2 < </prompt>");
        state = new State(prompt, 0);
        stack.save(state);

        assertTrue(stack.top().equals(state));

        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 4 || 0 < </prompt>");
        state = new State(prompt, 0);
        stack.save(state);

        //assertTrue(stack.previous().equals(state0));
*/
    }
/*
    @Test
    public void testRestore() throws Exception {
        CoqtopPrompt prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 1 || 0 < </prompt>");

        State state0 = new State(prompt, 0);
        Stack stack = new Stack(state0);

        assertTrue(stack.top().equals(state0));

        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 2 || 1 < </prompt>");
        State state1 = new State(prompt, 0);
        stack.save(state1);
        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 3 || 2 < </prompt>");

        State state2 = new State(prompt, 0);
        stack.save(state2);

        assertTrue(stack.top().equals(state2));

        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 4 || 0 < </prompt>");
        State state3 = new State(prompt, 0);
        stack.save(state3);

        stack.stepBack(1);
        assertTrue(stack.top().equals(state0));

    }
*/
/*    @Test
    public void testTop() throws Exception {

        CoqtopPrompt prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 1 || 0 < </prompt>");

        State state0 = new State(prompt, 0);
        Stack stack = new Stack(state0);

        assertTrue(stack.top().equals(state0));

    }

    @Test
    public void testHasPrevious() throws Exception {
        CoqtopPrompt prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 1 || 0 < </prompt>");

        State state0 = new State(prompt, 0);
        Stack stack = new Stack(state0);

        assertTrue(!stack.hasPreviousStep());

        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 2 || 1 < </prompt>");
        State state1 = new State(prompt, 0);
        stack.save(state1);

        assertTrue(stack.hasPreviousStep());

    }

    @Test
    public void testPrevious() throws Exception {
        CoqtopPrompt prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 1 || 0 < </prompt>");

        State state0 = new State(prompt, 0);
        Stack stack = new Stack(state0);

        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 2 || 1 < </prompt>");
        State state1 = new State(prompt, 0);
        stack.save(state1);

        //assertTrue(stack.previous().equals(state0));
    }
    */
}