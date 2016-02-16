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

package org.univorleans.coq.coqtop.ui;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.univorleans.coq.coqtop.Engine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by dabrowski on 26/01/2016.
 */
public class CoqtopMessageView implements ToolWindowFactory{


    private JButton button1, button2, button3, button4, button5, button6;
    private JPanel panel1;
    private JTextArea textArea1;
    private JTextArea textArea2;
    private ToolWindow myToolWindow;

    Project project;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        myToolWindow = toolWindow;

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel1, "", false);
        myToolWindow.getContentManager().addContent(content);
        this.project = project;
        Message.INSTANCE.setMessaveView(this);

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    Editor editor = fileEditorManager.getSelectedTextEditor();
                    Engine engine = Engine.getEngine(editor);
                    if (engine == null) return;
                    engine.next();
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    Editor editor = fileEditorManager.getSelectedTextEditor();
                    Engine engine = Engine.getEngine(editor);
                    if (engine != null) return;
                    engine.undo();
            }
        });

        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    Editor editor = fileEditorManager.getSelectedTextEditor();
                    Engine engine = Engine.getEngine(editor);
                    if (engine != null) return;
                    engine.use();
            }
        });

        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    Editor editor = fileEditorManager.getSelectedTextEditor();
                    Engine engine = Engine.getEngine(editor);
                    engine.retract();
            }
        });

        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                   Editor editor = fileEditorManager.getSelectedTextEditor();
                    Engine engine = Engine.getEngine(editor);
                    engine.gotoCursor();

            }
        });

        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    Editor editor = fileEditorManager.getSelectedTextEditor();
                    Engine engine = Engine.getEngine(editor);
                    engine.stop();
            }
        });
    }

    public void setText(String txt) {

        textArea1.setText(txt);
    }

    public void setInfoText(String txt){
        textArea2.setText(txt);
    }
}
