package org.univorleans.coq.toplevel;

import com.intellij.mock.MockDocument;
import com.intellij.openapi.editor.Document;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dabrowski on 29/01/2016.
 */
public class CoqDocumentIteratorTest {

    @Test
    public void testHasNext() throws Exception {

        Document document;
        CoqDocumentIterator iterator;

        document = new MockDocument("Require Import List.\n");
        iterator = new CoqDocumentIterator(document);
        assertTrue(iterator.hasNext(0));

        document = new MockDocument("Require Import List.");
        iterator = new CoqDocumentIterator(document);
        assertTrue(iterator.hasNext(0));

        document = new MockDocument("Require Import List.ANY");
        iterator = new CoqDocumentIterator(document);
        assertFalse(iterator.hasNext(0));

        document = new MockDocument("Require Import List. Require Import Set.");
        iterator = new CoqDocumentIterator(document);
        assertTrue(iterator.hasNext(20));

        document = new MockDocument("(* . Require Import Set.");
        iterator = new CoqDocumentIterator(document);
        assertFalse(iterator.hasNext(0));

        document = new MockDocument("*) Require Import Set.");
        iterator = new CoqDocumentIterator(document);
        assertTrue(iterator.hasNext(0));
    }

    @Test
    public void testNext() throws Exception {

        Document document;
        CoqDocumentIterator iterator;
        String str;

        document = new MockDocument("Require Import List.\n");
        iterator = new CoqDocumentIterator(document);
        str = iterator.next(0);
        assertTrue(str.equals("Require Import List."));
        assertTrue(iterator.getOffset() == 20);

        document = new MockDocument("Require Import List.");
        iterator = new CoqDocumentIterator(document);
        str = iterator.next(0);
        assertTrue(str.equals("Require Import List."));
        assertTrue(iterator.getOffset() == 20);

        document = new MockDocument("Require Import List. Require Import Set.");
        iterator = new CoqDocumentIterator(document);
        str = iterator.next(20);
        assertTrue(str.equals("Require Import Set."));
        assertTrue(iterator.getOffset() == 40);

        document = new MockDocument("Require Import List. (* . *) Require Import Set.");
        iterator = new CoqDocumentIterator(document);
        str = iterator.next(20);
        assertTrue(str.equals("(* . *) Require Import Set."));

        document = new MockDocument("*) Require Import Set.");
        iterator = new CoqDocumentIterator(document);
        str = iterator.next(0);
        assertTrue(str.equals("*) Require Import Set."));

    }
}