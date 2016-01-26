package org.univorleans.coq.toolWindows;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.univorleans.coq.errors.InvalidPrompt;
import org.univorleans.coq.errors.InvalidState;
import org.univorleans.coq.errors.NoCoqProcess;
import org.univorleans.coq.listeners.MessageTextListener;
import org.univorleans.coq.listeners.ProofTextListener;
import org.univorleans.coq.toplevel.CoqState;
import org.univorleans.coq.toplevel.CoqStateListener;
import org.univorleans.coq.toplevel.CoqtopEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by dabrowski on 26/01/2016.
 */
public class CoqtopMessageView implements ToolWindowFactory, MessageTextListener, CoqStateListener {


    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JButton button6;
    private JPanel panel1;
    private ToolWindow myToolWindow;

    Project project;


    @Override
    public void messageViewChangee(String txt) {

    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        myToolWindow = toolWindow;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel1, "", false);
        myToolWindow.getContentManager().addContent(content);
        this.project = project;

        // A ajouter a chaque editor
 /*       Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        Document document = editor.getDocument();
        CoqtopEngine coqtopEngine = null;
        try {
            coqtopEngine = CoqtopEngine.getEngine(editor);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidPrompt invalidPrompt) {
            invalidPrompt.printStackTrace();
        } catch (InvalidState invalidState) {
            invalidState.printStackTrace();
        }
        if (coqtopEngine != null) {
            coqtopEngine.addMessageViewListener(this);
            coqtopEngine.addCoqStateListener(this);
        }
        else CoqtopEngine.messageView = this;*/


        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    CoqtopEngine coqtopEngine = CoqtopEngine.getEngine(editor);
                    if (coqtopEngine == null) return;
                    coqtopEngine.next();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (NoCoqProcess noCoqProcess) {
                    noCoqProcess.printStackTrace();
                } catch (InvalidPrompt invalidPrompt) {
                    invalidPrompt.printStackTrace();
                } catch (NullPointerException e2){
                    e2.printStackTrace();
                } catch (InvalidState invalidState) {
                    invalidState.printStackTrace();
                }
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    CoqtopEngine coqtopEngine = CoqtopEngine.getEngine(editor);
                    coqtopEngine.undo();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (NoCoqProcess noCoqProcess) {
                    noCoqProcess.printStackTrace();
                } catch (InvalidPrompt invalidPrompt) {
                    invalidPrompt.printStackTrace();
                } catch (NullPointerException e2){
                    e2.printStackTrace();
                } catch (InvalidState invalidState) {
                    invalidState.printStackTrace();
                }
            }
        });
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    CoqtopEngine coqtopEngine = CoqtopEngine.getEngine(editor);
                    coqtopEngine.use();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (NoCoqProcess noCoqProcess) {
                    noCoqProcess.printStackTrace();
                } catch (InvalidPrompt invalidPrompt) {
                    invalidPrompt.printStackTrace();
                } catch (NullPointerException e2){
                    e2.printStackTrace();
                } catch (InvalidState invalidState) {
                    invalidState.printStackTrace();
                }
            }
        });
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    CoqtopEngine coqtopEngine = CoqtopEngine.getEngine(editor);
                    coqtopEngine.retract();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (NoCoqProcess noCoqProcess) {
                    noCoqProcess.printStackTrace();
                } catch (InvalidPrompt invalidPrompt) {
                    invalidPrompt.printStackTrace();
                } catch (NullPointerException e2){
                    e2.printStackTrace();
                } catch (InvalidState invalidState) {
                    invalidState.printStackTrace();
                }
            }
        });
        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    CoqtopEngine coqtopEngine = CoqtopEngine.getEngine(editor);
                    coqtopEngine.gotoCursor();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InvalidPrompt invalidPrompt) {
                    invalidPrompt.printStackTrace();
                } catch (NullPointerException e2){
                    e2.printStackTrace();
                } catch (InvalidState invalidState) {
                    invalidState.printStackTrace();
                }
            }
        });
        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    CoqtopEngine coqtopEngine = CoqtopEngine.getEngine(editor);
                    coqtopEngine.stop();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InvalidPrompt invalidPrompt) {
                    invalidPrompt.printStackTrace();
                } catch (NullPointerException e2){
                    e2.printStackTrace();
                } catch (InvalidState invalidState) {
                    invalidState.printStackTrace();
                }
            }
        });
    }

    @Override
    public void coqStateChangee(CoqState c) {

    }
}
