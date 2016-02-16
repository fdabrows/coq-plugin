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

package org.univorleans.coq.files.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.util.CoqIcons;

/**
 * Created by dabrowski on 20/01/2016.
 */
public class NewCoqFileAction extends CreateFileFromTemplateAction {

    private static final String NEW_COQ_MODULE = "New Coq Module";

    public NewCoqFileAction() {

        super(null, null, null);
    }


    @Override
    protected void buildDialog(Project project, PsiDirectory psiDirectory, CreateFileFromTemplateDialog.Builder builder) {
        builder.
                setTitle(NEW_COQ_MODULE).
                addKind("Empty module", CoqIcons.FILE, "Coq Module").
                setValidator(new InputValidatorEx() {
                    @Override
                    public boolean checkInput(String inputString) {

                        return getErrorText(inputString) == null;
                    }

                    @Override
                    public boolean canClose(String inputString) {

                        return getErrorText(inputString) == null;
                    }

                    @Nullable
                    @Override
                    public String getErrorText(String inputString) {

                        return null;
                    }
                });
    }

    @Override
    protected String getActionName(PsiDirectory psiDirectory, String s, String s1) {
        return NEW_COQ_MODULE;
    }
}
