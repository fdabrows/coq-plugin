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

package org.univorleans.coq.structure;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.psi.*;
import org.univorleans.coq.util.CoqIcons;

import javax.swing.*;

/**
 * Created by dabrowski on 12/02/2016.
 */
public class CoqItemPresentation implements ItemPresentation {

    private PsiElement element;

    public CoqItemPresentation(PsiElement element) {
        this.element = element;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        if (element instanceof CoqFile)
            return ((CoqFile) element).getName();
        else if (element instanceof CoqModule)
            return CoqPsiImpUtil.getName((CoqModule) element);
        else if (element instanceof CoqSection)
            return CoqPsiImpUtil.getName((CoqSection) element);
        else if (element instanceof CoqDefinition)
            return CoqPsiImpUtil.getName((CoqDefinition) element);
        else if (element instanceof CoqAssertion)
            return CoqPsiImpUtil.getName((CoqAssertion) element);
        else if (element instanceof CoqIndBody)
            return CoqPsiImpUtil.getName((CoqIndBody) element);
        else return null;
    }

    @Nullable
    @Override
    public String getLocationString() {
        return null;
    }

    @Nullable
    @Override
    public Icon getIcon(boolean b) {

        if (element instanceof CoqAssertion) {
            return CoqIcons.RESULT;
        } else if (element instanceof CoqIndBody || element instanceof CoqInductive) {
            return CoqIcons.DEFINITION;
        } else if (element instanceof CoqDefinition) {
            return CoqIcons.DEFINITION;
        } else if (element instanceof CoqModule) {
            return CoqIcons.MODULE;
        } else if (element instanceof CoqSection) {
            return CoqIcons.SECTION;
        } else return CoqIcons.FILE;
    }
}

