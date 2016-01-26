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

package org.univorleans.coq.highlighter;

import com.intellij.lang.CodeDocumentationAwareCommenter;
import com.intellij.psi.PsiComment;
import com.intellij.psi.tree.IElementType;
import org.univorleans.coq.psi.CoqTypes;
import org.jetbrains.annotations.Nullable;

/**
 * Created by dabrowski on 13/01/2016.
 */
public class CoqCommenter implements CodeDocumentationAwareCommenter {
    @Nullable
    @Override
    public IElementType getLineCommentTokenType() {
        return CoqTypes.COMMENTARY;
    }

    @Nullable
    @Override
    public IElementType getBlockCommentTokenType() {
        return CoqTypes.COMMENTARY;
    }

    @Nullable
    @Override
    public IElementType getDocumentationCommentTokenType() {
        return CoqTypes.DOCUMENTATION;
    }

    @Nullable
    @Override
    public String getDocumentationCommentPrefix() {
        return "(**";
    }

    @Nullable
    @Override
    public String getDocumentationCommentLinePrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getDocumentationCommentSuffix() {
        return "*)";
    }

    @Override
    public boolean isDocumentationComment(PsiComment psiComment) {
        return psiComment.getTokenType() == CoqTypes.DOCUMENTATION;
    }

    @Nullable
    @Override
    public String getLineCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getBlockCommentPrefix() {
        return "(*";
    }

    @Nullable
    @Override
    public String getBlockCommentSuffix() {
        return "*)";
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentSuffix() {
        return null;
    }
}
