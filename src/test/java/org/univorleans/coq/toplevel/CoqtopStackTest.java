package org.univorleans.coq.toplevel;

import org.junit.Test;
import org.univorleans.coq.toolWindows.CoqtopMessage;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by dabrowski on 29/01/2016.
 */
public class CoqtopStackTest {

    @Test
    public void testSave() throws Exception {

        CoqtopPrompt prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 1 || 0 < </prompt>");

        CoqtopState state0 = new CoqtopState(prompt, 0);
        CoqtopStack stack = new CoqtopStack();
        stack.save(state0);

        assertTrue(stack.top().equals(state0));

        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 2 || 1 < </prompt>");
        CoqtopState state = new CoqtopState(prompt, 0);
        stack.save(state);
        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 3 || 2 < </prompt>");
        state = new CoqtopState(prompt, 0);
        stack.save(state);

        assertTrue(stack.top().equals(state));

        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 4 || 0 < </prompt>");
        state = new CoqtopState(prompt, 0);
        stack.save(state);

        assertTrue(stack.previous().equals(state0));

    }

    @Test
    public void testRestore() throws Exception {
        CoqtopPrompt prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 1 || 0 < </prompt>");

        CoqtopState state0 = new CoqtopState(prompt, 0);
        CoqtopStack stack = new CoqtopStack();
        stack.save(state0);

        assertTrue(stack.top().equals(state0));

        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 2 || 1 < </prompt>");
        CoqtopState state1 = new CoqtopState(prompt, 0);
        stack.save(state1);
        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 3 || 2 < </prompt>");

        CoqtopState state2 = new CoqtopState(prompt, 0);
        stack.save(state2);

        assertTrue(stack.top().equals(state2));

        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 4 || 0 < </prompt>");
        CoqtopState state3 = new CoqtopState(prompt, 0);
        stack.save(state3);

        stack.restore(1);
        assertTrue(stack.top().equals(state0));

    }

    @Test
    public void testTop() throws Exception {

        CoqtopPrompt prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 1 || 0 < </prompt>");

        CoqtopState state0 = new CoqtopState(prompt, 0);
        CoqtopStack stack = new CoqtopStack();
        stack.save(state0);

        assertTrue(stack.top().equals(state0));

    }

    @Test
    public void testHasPrevious() throws Exception {
        CoqtopPrompt prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 1 || 0 < </prompt>");

        CoqtopState state0 = new CoqtopState(prompt, 0);
        CoqtopStack stack = new CoqtopStack();
        stack.save(state0);

        assertTrue(!stack.hasPrevious());

        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 2 || 1 < </prompt>");
        CoqtopState state1 = new CoqtopState(prompt, 0);
        stack.save(state1);

        assertTrue(stack.hasPrevious());

    }

    @Test
    public void testPrevious() throws Exception {
        CoqtopPrompt prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 1 || 0 < </prompt>");

        CoqtopState state0 = new CoqtopState(prompt, 0);
        CoqtopStack stack = new CoqtopStack();
        stack.save(state0);

        prompt = CoqtopPrompt.makePrompt("<prompt>Coq < 2 || 1 < </prompt>");
        CoqtopState state1 = new CoqtopState(prompt, 0);
        stack.save(state1);

        assertTrue(stack.previous().equals(state0));
    }
}