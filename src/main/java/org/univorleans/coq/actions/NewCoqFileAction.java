package org.univorleans.coq.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.CoqIcons;

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
