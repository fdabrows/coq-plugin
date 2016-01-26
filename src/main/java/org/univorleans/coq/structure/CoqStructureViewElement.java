/*
 * IntelliJ-coqplugin  / Plugin IntelliJ for Coq
 * Copyright (c) 2016
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
import org.univorleans.coq.util.CoqPsiImpUtil;
import org.univorleans.coq.util.CoqIcons;
import org.univorleans.coq.psi.*;
import org.univorleans.coq.psi.CoqFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
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
                ((NavigationItem)element).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return element instanceof NavigationItem &&
                ((NavigationItem)element).canNavigateToSource();
    }

    @Override
    public String getAlphaSortKey() {
        return element instanceof PsiNamedElement ? ((PsiNamedElement) element).getName() : null;
    }

    public class MyPresentation implements ItemPresentation{

        private String text1;
        private String text2;

        public MyPresentation(String text1, String text2){
            this.text1=text1;
            this.text2=text2;
        }

        @Nullable
        @Override
        public String getPresentableText() {
            return text1;

        }

        @Nullable
        @Override
        public String getLocationString() {
            return text2;
        }

        @Nullable
        @Override
        public Icon getIcon(boolean b) {
            if (element instanceof CoqAssertion) {
                return CoqIcons.FILE;
            } else if (element instanceof CoqFixBody) {
                return CoqIcons.FILE;
            } else if (element instanceof CoqIndBody) {
                return CoqIcons.FILE;
            } else if (element instanceof CoqDefinition) {
                return CoqIcons.FILE;
            } else if (element instanceof CoqInductive) {
                return CoqIcons.FILE;
            }
            else return CoqIcons.FILE;
        }
    }

    @Override
    public ItemPresentation getPresentation() {
        if (element instanceof CoqAssertion) {
            String kind = CoqPsiImpUtil.getKey((CoqAssertion) element);
            String name = CoqPsiImpUtil.getValue((CoqAssertion) element);
            return new MyPresentation(name, kind);
        }
        else if (element instanceof CoqFixBody){
            String name = CoqPsiImpUtil.getName((CoqFixBody) element);
            return new MyPresentation(name, null);
        }
        else if (element instanceof CoqIndBody){
            String name = CoqPsiImpUtil.getName((CoqIndBody) element);
            return new MyPresentation(name, null);
        }

        else if (element instanceof CoqDefinition){
            String name = CoqPsiImpUtil.getName((CoqDefinition) element);
            return new MyPresentation(name, null);
        }
        else if (element instanceof CoqFile) {
            String name = CoqPsiImpUtil.getKey((CoqFile) element);
            return new MyPresentation("File " + name, "File");
        } else
        {
            System.out.println(element.getText());
            return new MyPresentation("Bidon", "Bidon");
        }
    }

    @Override
    public TreeElement[] getChildren() {
        if (element instanceof CoqFile) {
            List<TreeElement> treeElements = new ArrayList<>();
            PsiElement[] elements = element.getChildren();
            for (int i = 0; i < elements.length;i++) {
                if (CoqDefGeneral.class.isInstance(elements[i])) {
                    PsiElement[] elements2 = elements[i].getChildren();
                    for (int j = 0; j < elements2.length; j++) {
                        if(CoqFixpoint.class.isInstance(elements2[j])) {
                            PsiElement[] elements3 = elements2[j].getChildren();
                           for (int k = 0; k < elements3.length; k++) {
                               if(CoqFixBody.class.isInstance(elements3[k])) {
                                   treeElements.add(new CoqStructureViewElement(elements3[k]));
                               }
                           }
                        } else if (CoqInductive.class.isInstance(elements2[j])){
                            PsiElement[] elements3 = elements2[j].getChildren();
                            for (int k = 0; k < elements3.length; k++) {
                                if (CoqIndBody.class.isInstance(elements3[k])) {
                                    treeElements.add(new CoqStructureViewElement(elements3[k]));
                                }
                            }
                        } else if (CoqDefinition.class.isInstance(elements2[j])){
                            treeElements.add(new CoqStructureViewElement(elements2[j]));
                        } else if (CoqAssertion.class.isInstance(elements2[j])){
                            treeElements.add(new CoqStructureViewElement(elements2[j]));
                        }
                    }
                }
            }
            return treeElements.toArray(new TreeElement[treeElements.size()]);
        } else {
            return EMPTY_ARRAY;
        }

    }
}
