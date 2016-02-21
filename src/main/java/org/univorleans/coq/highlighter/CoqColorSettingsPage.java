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

package org.univorleans.coq.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.univorleans.coq.util.CoqIcons;
import org.univorleans.coq.highlighter.CoqSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class CoqColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{

            new AttributesDescriptor("Commands", CoqSyntaxHighlighter.COMMAND),
            new AttributesDescriptor("CoqKeywords", CoqSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Kinds", CoqSyntaxHighlighter.KIND),
            new AttributesDescriptor("Names", CoqSyntaxHighlighter.NAME),
            new AttributesDescriptor("Terms", CoqSyntaxHighlighter.FORMULAE),
            new AttributesDescriptor("Tactics", CoqSyntaxHighlighter.TACTIC),

    };

    @Nullable
    @Override
    public Icon getIcon() {
        return CoqIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {

        return new CoqSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return
                "Require Import Compare_dec.\n\n"+
                "Inductive bop : Set :=\n"+
                "Add | Sub | Mul | Eq | Neq | Lt |Gt | Le | Ge | And | Or.\n\n"+
                "Inductive expr : Set :=\n"+
                "| Const : nat -> expr\n" + "| Var : string -> expr\n"+
                "| Bop : bop -> expr -> expr -> expr.\n\n"+
                "Definition update (x : string) (n : nat) (s : state) : state :=\n"+
                        "    fun y => if string_dec x y then n else s x.\n\n" +
                 "Fact nth_error_nil_some :\n"+
                 "  forall (A : Type)  (i : nat) (X : A),\n"+
                 "    nth_error nil i = Some X -> False.\n"+
                 "Proof.\n"+
                 "  intros A i X H_eq.\n"+
                 "  destruct i; discriminate.\n"+
                 "Qed.\n";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {

        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {

        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Coq";
    }
}
