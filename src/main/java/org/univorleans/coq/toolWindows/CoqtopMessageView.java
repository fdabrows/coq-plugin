/*
 * IntelliJ-coqplugin  / Plugin IntelliJ for Coq
 * Copyright (c) 2016 F. Dabrowski
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.univorleans.coq.toolWindows;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.univorleans.coq.errors.InvalidCoqtopResponse;
import org.univorleans.coq.errors.NoCoqProcess;
import org.univorleans.coq.toplevel.CoqtopEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by dabrowski on 26/01/2016.
 */
public class CoqtopMessageView implements ToolWindowFactory{


    private JButton button1, button2, button3, button4, button5, button6;
    private JPanel panel1;
    private JTextArea textArea1;
    private ToolWindow myToolWindow;

    Project project;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        myToolWindow = toolWindow;

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel1, "", false);
        myToolWindow.getContentManager().addContent(content);
        this.project = project;
        CoqtopMessage.INSTANCE.setMessaveView(this);

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    Editor editor = fileEditorManager.getSelectedTextEditor();
                    CoqtopEngine coqtopEngine = CoqtopEngine.getEngine(editor);
                    if (coqtopEngine == null) return;
                    coqtopEngine.next();
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    Editor editor = fileEditorManager.getSelectedTextEditor();
                    CoqtopEngine coqtopEngine = CoqtopEngine.getEngine(editor);
                    if (coqtopEngine != null) return;
                    coqtopEngine.undo();
            }
        });

        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    Editor editor = fileEditorManager.getSelectedTextEditor();
                    CoqtopEngine coqtopEngine = CoqtopEngine.getEngine(editor);
                    if (coqtopEngine != null) return;
                    coqtopEngine.use();
            }
        });

        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    Editor editor = fileEditorManager.getSelectedTextEditor();
                    CoqtopEngine coqtopEngine = CoqtopEngine.getEngine(editor);
                    coqtopEngine.retract();
            }
        });

        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                   Editor editor = fileEditorManager.getSelectedTextEditor();
                    CoqtopEngine coqtopEngine = CoqtopEngine.getEngine(editor);
                    coqtopEngine.gotoCursor();

            }
        });

        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    Editor editor = fileEditorManager.getSelectedTextEditor();
                    CoqtopEngine coqtopEngine = CoqtopEngine.getEngine(editor);
                    coqtopEngine.stop();
            }
        });
    }

    public void setText(String txt) {

        textArea1.setText(txt);
    }
}
