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
package org.univorleans.coq.coqtop.actions;

import com.intellij.openapi.actionSystem.*;
import org.univorleans.coq.coqtop.Engine;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;

/**
 * Created by dabrowski on 05/01/2016.
 */
public class RetractAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {

        Editor editor = FileEditorManager.getInstance(e.getProject()).getSelectedTextEditor();
        if (editor != null) {
            Engine engine = Engine.getEngine(editor);
            if (engine != null) engine.retract();
        }
    }

}
