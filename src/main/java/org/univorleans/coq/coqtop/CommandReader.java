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

package org.univorleans.coq.coqtop;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.Nullable;

/**
 * Created by dabrowski on 29/01/2016.
 */
public class CommandReader {

    /**
     * Computes the position, if exists, of the end of the next command
     * starting at a given offset in a document.
     *
     * @param document    the searched document
     * @param startOffset the search start offset
     * @return the offset of the end of the next command starting from
     * startOffset in document if exists, -1 otherwise
     */
    public static int hasNext(Document document, int startOffset) {

        String txt = document.getText();
        int offset = startOffset;
        int cpt = 0;

        while (offset < txt.length()) {
            switch (txt.charAt(offset)) {
                case ' ':
                case '\n':
                case '\t':
                case '\r':
                    break;
                case '(':
                    if (offset + 1 < txt.length() && txt.charAt(offset + 1) == '*') cpt++;
                    break;
                case '-':
                    if (cpt <= 0) {
                        while (offset + 1 < txt.length() && txt.charAt(offset + 1) == '-') offset++;
                        return offset + 1;
                    }
                    break;
                case '+':
                    if (cpt <= 0) {
                        while (offset + 1 < txt.length() && txt.charAt(offset + 1) == '+') offset++;
                        return offset + 1;
                    }
                    break;
                case '*':
                    if (cpt <= 0) {
                        while (offset + 1 < txt.length() && txt.charAt(offset + 1) == '*') offset++;
                        return offset + 1;
                    } else if (offset + 1 < txt.length() && txt.charAt(offset + 1) == ')') cpt--;
                    break;
                default:
                    if (cpt <= 0) {
                        int endOffset = txt.indexOf('.', startOffset);
                        while (endOffset >= 0) {
                            String cmd = document.getText(new TextRange(startOffset, endOffset + 1));
                            if (endOffset < txt.length() - 1) {
                                char c = txt.charAt(endOffset + 1);
                                if (isValid(cmd) && Character.isWhitespace(c)) return endOffset + 1;
                                else endOffset = txt.indexOf('.', endOffset+1);
                            } else if (isValid(cmd)) return endOffset + 1;
                        }
                        return -1;
                    }
                    break;
            }
            offset++;
        }
        return -1;
    }

    /**
     * Return a command from a given range in some document
     *
     * @param document the searched document
     * @param range    the range
     * @return a string command if the range is valid
     */
    @Nullable
    public static String getCommand(Document document, TextRange range) {
        return document.getText(range).replace('\n', ' ').trim();
    }

    private static boolean isValid(String cmd) {
        if (cmd.length() > 1 && cmd.charAt(cmd.length() - 2) == '.')
            return false;
        int cpt = 0;
        for (int i = 0; i < cmd.length() - 1; i++) {
            if (cmd.charAt(i) == '(' && cmd.charAt(i + 1) == '*') cpt++;
            if (cmd.charAt(i) == '*' && cmd.charAt(i + 1) == ')') cpt--;
        }
        return (cpt <= 0);
    }
}
