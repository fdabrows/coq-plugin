package org.univorleans.coq.coqtop;

import com.intellij.mock.MockDocument;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dabrowski on 29/01/2016.
 */
public class CoqCommandReaderTest {

    @Test
    public void testHasNext() throws Exception {

        Document document;
        CommandReader iterator;

        document = new MockDocument("Require Import List.\n");
        assertTrue(CommandReader.hasNext(document, 0) == 20);

        document = new MockDocument("Require Import List.");
        assertTrue(CommandReader.hasNext(document, 0) == 20);

        document = new MockDocument("Require Import List.ANY");
        assertTrue(CommandReader.hasNext(document, 0) < 0);

        document = new MockDocument("Require Import List. Require Import Set.");
        assertTrue(CommandReader.hasNext(document, 20) == 40);

        document = new MockDocument("(* . Require Import Set.");
        assertTrue(CommandReader.hasNext(document, 0) < 0);

        document = new MockDocument("*) Require Import Set.");
        assertTrue(CommandReader.hasNext(document, 0) == 1);

        document = new MockDocument("-");
        assertTrue(CommandReader.hasNext(document, 0) == 1);

        document = new MockDocument(" -");
        assertTrue(CommandReader.hasNext(document, 0) > 0);

        document = new MockDocument("++");
        assertTrue(CommandReader.hasNext(document, 0) == 2);

        document = new MockDocument("Notation \" [ x ; .. ; y ] \" := (cons x .. (cons y nil) ..) : list_scope.");
        assertTrue(CommandReader.hasNext(document, 0) > 0);
    }

    @Test
    public void testNext() throws Exception {

        Document document;
        String str;

        document = new MockDocument("Require Import List.\n");
        int endOffset = CommandReader.hasNext(document, 0);
        str = CommandReader.getCommand(document, new TextRange(0, endOffset));
        assertTrue(str.equals("Require Import List."));

        document = new MockDocument("Require Import List.");
        endOffset = CommandReader.hasNext(document, 0);
        str = CommandReader.getCommand(document, new TextRange(0, endOffset));
        assertTrue(str.equals("Require Import List."));

        document = new MockDocument("Require Import List. Require Import Set.");
        endOffset = CommandReader.hasNext(document, 20);
        str = CommandReader.getCommand(document, new TextRange(20, endOffset));
        assertTrue(str.equals("Require Import Set."));

        document = new MockDocument("Require Import List. (* . *) Require Import Set.");
        endOffset = CommandReader.hasNext(document, 20);
        str = CommandReader.getCommand(document, new TextRange(20, endOffset));
        assertTrue(str.equals("(* . *) Require Import Set."));

        document = new MockDocument("*) Require Import Set.");
        endOffset = CommandReader.hasNext(document, 0);
        str = CommandReader.getCommand(document, new TextRange(0, endOffset));
        assertTrue(str.equals("*"));

        document = new MockDocument("-");
        endOffset = CommandReader.hasNext(document, 0);
        str = CommandReader.getCommand(document, new TextRange(0, endOffset));
        assertTrue(str.equals("-"));

        document = new MockDocument("++");
        endOffset = CommandReader.hasNext(document, 0);
        str = CommandReader.getCommand(document, new TextRange(0, endOffset));
        assertTrue(str.equals("++"));

        document = new MockDocument("*** intros.");
        endOffset = CommandReader.hasNext(document, 0);
        str = CommandReader.getCommand(document, new TextRange(0, endOffset));
        assertTrue(str.equals("***"));
        endOffset = CommandReader.hasNext(document, 3);
        str = CommandReader.getCommand(document, new TextRange(3, endOffset));
        assertTrue(str.equals("intros."));

        document = new MockDocument("Notation \" [ x ; .. ; y ] \" := (cons x .. (cons y nil) ..) : list_scope.");
        endOffset = CommandReader.hasNext(document, 0);
        str = CommandReader.getCommand(document, new TextRange(0, endOffset));
        assertTrue (str.equals("Notation \" [ x ; .. ; y ] \" := (cons x .. (cons y nil) ..) : list_scope."));
    }
}