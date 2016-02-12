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

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.univorleans.coq.psi.*;
import org.univorleans.coq.psi.CoqFile;

import java.util.ArrayList;
import java.util.List;

public class CoqStructureViewElement implements StructureViewTreeElement, SortableTreeElement {

    private PsiElement element;

    public CoqStructureViewElement(PsiElement element) {
        this.element = element;
    }

    @Override
    public Object getValue() {
        return element;
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (element instanceof NavigationItem) {
            ((NavigationItem) element).navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return element instanceof NavigationItem &&
                ((NavigationItem) element).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return element instanceof NavigationItem &&
                ((NavigationItem) element).canNavigateToSource();
    }

    @Override
    public String getAlphaSortKey() {
        return element instanceof PsiNamedElement ? ((PsiNamedElement) element).getName() : null;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new CoqItemPresentation(element);
    }

    @Override
    public TreeElement[] getChildren() {

        if (element instanceof CoqFile) {
            List<TreeElement> treeElements = children(element);
            return treeElements.toArray(new TreeElement[treeElements.size()]);
        }
        else if (element instanceof CoqModule || element instanceof CoqSection){
            List<TreeElement> treeElements = children(element);
            return treeElements.toArray(new TreeElement[treeElements.size()]);
        }
        else return EMPTY_ARRAY;
    }

    private List<TreeElement> children(PsiElement element) {
        List<TreeElement> treeElements = new ArrayList<>();
        PsiElement[] elements = element.getChildren();
        for (int i = 0; i < elements.length; i++) {

            if (CoqModule.class.isInstance(elements[i])) {

                treeElements.add(new CoqStructureViewElement(elements[i]));

            } else if (CoqSection.class.isInstance(elements[i])) {
                treeElements.add(new CoqStructureViewElement(elements[i]));

            } else
            if (CoqDefGeneral.class.isInstance(elements[i])) {

                treeElements.addAll(getDefGeneralChildren(elements[i]));
            }
        }
        return treeElements;
    }

    private List<TreeElement> getDefGeneralChildren(PsiElement element) {

        PsiElement child = element.getFirstChild();
        List<TreeElement> treeElements = new ArrayList<>();

        if (child instanceof CoqDefinition || child instanceof CoqAssertion) {

            treeElements.add(new CoqStructureViewElement(child));
        } else if (child instanceof CoqFixpoint || child instanceof CoqInductive) {

            PsiElement[] elements = child.getChildren();
            for (int i = 0; i < elements.length; i++) {
                if (CoqIndBody.class.isInstance(elements[i])) {
                    treeElements.add(new CoqStructureViewElement(elements[i]));
                }
            }
        }
        return treeElements;
    }
}
