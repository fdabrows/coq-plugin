package org.univorleans.coq.toplevel;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dabrowski on 27/01/2016.
 */
public class CoqtopPromptTest {

    @Test
    public void testMakePrompt1() throws Exception {

        String str = "<prompt>Coq < 4 || 3 < </prompt>";
        CoqtopPrompt prompt = CoqtopPrompt.makePrompt(str);
        assertTrue(prompt.getGlobalCounter() == 4 && prompt.getProofCounter() == 3);

    }

    @Test
    public void testMakePrompt2() throws Exception {

        String str = "<prompt>l1 < 4 |l1| 3 < </prompt>";
        CoqtopPrompt prompt = CoqtopPrompt.makePrompt(str);
        assertTrue(prompt.getGlobalCounter() == 4 && prompt.getProofCounter() == 3);

    }

    @Test
    public void testMakePrompt3() throws Exception {

        String str = "<prompt>l1 < 4 |l2|l1| 3 < </prompt>";
        CoqtopPrompt prompt = CoqtopPrompt.makePrompt(str);
        assertTrue(prompt.getGlobalCounter() == 4 && prompt.getProofCounter() == 3);

    }

}