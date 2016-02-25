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
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.psi.CoqTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dabrowski on 02/02/2016.
 */
public class CoqBlock extends AbstractBlock {


    private final Indent myIndent;
    private List<Block> mySubBlocks;

    public static final TokenSet BLOCKS_TOKEN_SET = TokenSet.create(
            CoqTypes.LEMMA_KW
    );

    public CoqBlock(@NotNull ASTNode node, @Nullable Wrap wrap,
                    @Nullable Alignment alignment, Indent current) {
        super(node, wrap, alignment);
        myIndent = new CoqIndentProcessor().getChildIndent(node, alignment, current);
    }


    @Override
    protected List<Block> buildChildren() {

        if (mySubBlocks == null) {
            mySubBlocks = buildSubBlocks();
        }
        return new ArrayList<>(mySubBlocks);
    }


    private List<Block> buildSubBlocks() {
        final List<Block> blocks = new ArrayList<Block>();
        Indent prevIndent = Indent.getNoneIndent();
        for (ASTNode child = myNode.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            if (!shouldCreateBlockFor(child)) continue;
            Block b = createChildBlock(child, Alignment.createAlignment(), prevIndent);
            prevIndent = b.getIndent();
            blocks.add(b);
        }
        return Collections.unmodifiableList(blocks);
    }

    private static boolean shouldCreateBlockFor(ASTNode node) {
        return node.getTextRange().getLength() != 0 &&
                node.getElementType() != TokenType.WHITE_SPACE;
    }


    private CoqBlock createChildBlock(ASTNode child, Alignment alignment, Indent current) {
        return new CoqBlock(child, null, alignment, current);
    }


    @Override
    public Indent getIndent() {
        return myIndent;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return null;
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

}
