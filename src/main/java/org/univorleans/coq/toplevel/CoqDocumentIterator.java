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

package org.univorleans.coq.toplevel;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;

/**
 * Created by dabrowski on 29/01/2016.
 */
public class CoqDocumentIterator {

    Document document;

    int nextOffset = 0;
    int start;

    public CoqDocumentIterator(Document document){
        this.document = document;
    }

    public boolean hasNext(int offset) {

        String txt = document.getText();
        int endOffset;
        char c;

        do{
            endOffset = txt.indexOf('.', offset);
            if (endOffset < 0) return false;
            if (endOffset == document.getTextLength() - 1) break;
            c = txt.charAt(endOffset + 1);
            offset = endOffset + 1;
        } while (!Character.isWhitespace(c));
        return true;
    }

    public String next(int offset) {

        String txt = document.getText();
        int endOffset;
        char c;

        do{
            start = offset;
            endOffset = txt.indexOf('.', offset);
            if (endOffset < 0) return null;
            if (endOffset == txt.length() - 1) break;
            c = txt.charAt(endOffset + 1);
            offset = endOffset + 1;
        } while (!Character.isWhitespace(c));
        nextOffset = endOffset+1;
        return document.getText(new TextRange(start, endOffset+1)).replace('\n', ' ').trim();
    }

    public int getOffset(){
        return nextOffset;
    }
}
