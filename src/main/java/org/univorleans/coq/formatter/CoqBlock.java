package org.univorleans.coq.formatter;

import com.intellij.formatting.*;
import com.intellij.formatting.alignment.AlignmentStrategy;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
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
    private Alignment alignment;
    private List<Block> mySubBlocks;

    public static final TokenSet BLOCKS_TOKEN_SET = TokenSet.create(
            CoqTypes.LEMMA_KW
    );

    public CoqBlock(@NotNull ASTNode node, @Nullable Wrap wrap,
                    @Nullable Alignment alignment, Indent current) {
        super(node, wrap, alignment);
        this.alignment = alignment;
        this.
        myIndent = new CoqIndentProcessor().getChildIndent(node, alignment, current);
    }


    @Override
    protected List<Block> buildChildren() {

        if (mySubBlocks == null) {
            mySubBlocks = buildSubBlocks();
        }
        return new ArrayList<Block>(mySubBlocks);
    }

    private List<Block> buildSubBlocks() {
        final List<Block> blocks = new ArrayList<Block>();
        Indent prevIndent = Indent.getNoneIndent();
//        if (myNode.getElementType() == CoqTypes.PROOFPHRASE) return Collections.unmodifiableList(blocks);
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
        //&& node.getElementType() != CoqTypes.DOT;
    }


    private CoqBlock createChildBlock(ASTNode child, Alignment alignment, Indent current) {
      /*  if (child.getElementType() == CoqTypes.ANY)
            return new CoqBlock(child.getFirstChildNode(), null, alignment);
        if (child.getElementType() == CoqTypes.DEF_GENERAL)
            return new CoqBlock(child.getFirstChildNode(), null, alignment);*/
        return new CoqBlock(child, null, alignment, current);
    }


    @Override
    public Indent getIndent() {
        //System.out.println(myIndent.toString()+" "+ myNode.toString() +";");
        return myIndent;
        //return Indent.getSpaceIndent(10);
//        return Indent.getNoneIndent();
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return null;
//        return mySpacingBuilder.getSpacing(this, child1, child2);
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

}
