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
import org.univorleans.coq.listeners.ProofTextListener;
import org.univorleans.coq.toplevel.CoqtopEngine;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by dabrowski on 26/01/2016.
 */
public class CoqtopProofView implements ToolWindowFactory, ProofTextListener {

    ToolWindow myToolWindow;
    private JPanel panel1;
    private JTextArea textArea1;

    @Override
    public void proofViewChangee(String txt) {
        textArea1.setText(txt);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        myToolWindow = toolWindow;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel1, "", false);
        myToolWindow.getContentManager().addContent(content);
        // A ajouter a chaque editor
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
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
        if (coqtopEngine != null) coqtopEngine.addProofViewListener(this);
        else CoqtopEngine.proofView = this;

    }
}
