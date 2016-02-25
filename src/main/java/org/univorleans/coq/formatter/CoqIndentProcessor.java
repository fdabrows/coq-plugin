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

package org.univorleans.coq.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Key;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.psi.CoqTypes;

/**
 * Created by dabrowski on 02/02/2016.
 */
public class CoqIndentProcessor {


    public CoqIndentProcessor() {

    }

    public Indent getChildIndent(ASTNode node, Alignment alignment, Indent currentIndent) {

        IElementType elementType = node.getElementType();
        ASTNode parent = node.getTreeParent();
        IElementType parentType = parent != null ? parent.getElementType() : null;
        ASTNode grandfather = parent != null ? parent.getTreeParent() : null;
        IElementType grandfatherType = grandfather != null ? grandfather.getElementType() : null;
        ASTNode prevSibling = FormatterUtil.getPreviousNonWhitespaceSibling(node);
        IElementType prevSiblingElementType = prevSibling != null ? prevSibling.getElementType() : null;

        // None
        if (parent == null || parent.getTreeParent() == null) {
            return Indent.getNoneIndent();
        }
        if (elementType == CoqTypes.MODULE || elementType == CoqTypes.SECTION) {
            return Indent.getNormalIndent();
        }
        if (elementType == CoqTypes.DEF_GENERAL) {
            return Indent.getNormalIndent();
        }
        if (elementType == CoqTypes.FORMULAE && elementType == CoqTypes.COMMAND) {
            return Indent.getNoneIndent();
        }
        if (elementType == CoqTypes.FORMULAE)
            if (parentType != CoqTypes.DEF_GENERAL && parentType != CoqTypes.PROOFPHRASE)
            return Indent.getNormalIndent();
        if (elementType == CoqTypes.PROOF) {
            return Indent.getNormalIndent();
        }
        if (elementType == CoqTypes.COMMENTARY || elementType == CoqTypes.DOCUMENTATION) {
            return Indent.getNormalIndent();
        }
        if (elementType == CoqTypes.FORALLQT || elementType == CoqTypes.EXISTSQT) {
            return Indent.getNormalIndent();
        }
        if (elementType == CoqTypes.PROOFPHRASE) {
            // Multiplicate offset by the right size.
            int tabsize = 2;
            if (isBullet(node.getText())) {
                int offset = bulletoffset(node.getText());
                return Indent.getSpaceIndent(tabsize * (offset + 2), true);
            } else if (isBullet(prevSibling.getText())) {
                int offset = tabsize * (bulletoffset(prevSibling.getText()) + 2);
                int size = sizeOfBullet(prevSibling.getText());
                offset += size;
                offset = (size > 0) ? offset + 1 : offset;
                return Indent.getSpaceIndent(offset, true);
            }
            else return Indent.getNormalIndent();
//            if (prevSiblingElementType == CoqTypes.PROOFENTER)
//                return Indent.getNormalIndent();
//            else return Indent.getNormalIndent();
            //else if (currentIndent != null) return currentIndent;
        }
        // Normal
        if (prevSibling != null && prevSibling.getText().equals("=>")) return Indent.getNormalIndent();
        //if (prevSibling != null && prevSibling.getElementType() == CoqTypes.ANY) {
        //    return Indent.getNormalIndent();
        //}

        return Indent.getNoneIndent();

    }

    public static boolean isBullet(String str) {
        if (str.length() > 0)
            switch (str.charAt(0)) {
                case '-':
                case '+':
                case '*':
                    return true;
            }
        return false;
    }

    private static int bulletoffset(String str) {
        int r = 0;
        if (str.length() > 0) {
            switch (str.charAt(0)) {
                case '-':
                    r = 0;
                    break;
                case '+':
                    r += 2;
                    break;
                case '*':
                    r += 4;
                    break;
                default:
                    break;
            }
            int add = 2;
            for (int i = 1; i < str.length(); i++) {
                switch (str.charAt(i)) {
                    case '-':
                        r = r + 3 * add;
                        break;
                    case '+':
                        r = r + 3 * add + 1;
                        break;
                    case '*':
                        r = r + 3 * add + 2;
                        break;
                    default:
                        return r;
                }
                add += 1;
            }
        }
        return r;
    }

    private static int sizeOfBullet(String str) {
        int r = 0;
        for (int i = 0; i < str.length(); i++) {

            switch (str.charAt(i)) {
                case '-':
                    r += 1;
                    break;
                case '+':
                    r += 1;
                    break;
                case '*':
                    r += 1;
                    break;
                default:
                    return r;
            }
        }
        return r;
    }


    private static boolean needIndent(@Nullable IElementType type) {
        return type != null && CoqBlock.BLOCKS_TOKEN_SET.contains(type);
    }
}