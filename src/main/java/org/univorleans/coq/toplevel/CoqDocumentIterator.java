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



    public boolean hasNextCommand(int offset) {

        String txt = document.getText();
        int endOffset;
        char c;

        String cmd;
        start = offset;

        do{
            endOffset = txt.indexOf('.', offset);
            if (endOffset < 0) return false;
            cmd = document.getText(new TextRange(start, endOffset+1)).replace('\n', ' ').trim();
            if (endOffset == document.getTextLength() - 1)
                if (isCommand(cmd)) return true; else return false;
            c = txt.charAt(endOffset + 1);
            offset = endOffset + 1;
        } while (!Character.isWhitespace(c) || ! isCommand(cmd));
        return true;
    }

    public boolean hasNext(int startOffset){
        int offset = startOffset;
        String txt = document.getText();
        int cpt = 0;
        Character c;
        while (offset < txt.length()){
            switch (txt.charAt(offset) ){
                case '(':
                    if (offset + 1 < txt.length() && txt.charAt(offset+1)=='*') cpt++;
                    break;
                case '*':
                    if (cpt > 0) {
                        if (offset + 1 < txt.length() && txt.charAt(offset + 1) == ')') cpt--;
                    } else {
                        while (offset + 1 < txt.length() && txt.charAt(offset+1)=='*') offset++;
                        return true;
                    }
                    break;
                case '-':
                    if (cpt <= 0) {
                        while (offset + 1 < txt.length() && txt.charAt(offset + 1) == '-') offset++;
                        return true;
                    }
                    break;
                case '+':
                    if (cpt <= 0) {
                        while (offset + 1 < txt.length() && txt.charAt(offset + 1) == '+') offset++;
                        return true;
                    }
                    break;
                default:
                    if (cpt <= 0) return hasNextCommand(startOffset);
            }
            offset++;
        }
        return false;
    }

    public String nextCommand(int offset) {

        String txt = document.getText();
        int endOffset;
        char c;
        String cmd;
        start = offset;

        do{
            endOffset = txt.indexOf('.', offset);
            if (endOffset < 0) return null;
            cmd = document.getText(new TextRange(start, endOffset+1)).replace('\n', ' ').trim();
            if (endOffset == txt.length() - 1 && isCommand(cmd)) break;
            c = txt.charAt(endOffset + 1);
            offset = endOffset + 1;

        } while (!Character.isWhitespace(c) || ! isCommand(cmd));

        nextOffset = endOffset+1;
        return cmd;
    }

    public String next(int startOffset){

        int offset = startOffset;
        String txt = document.getText();
        int cpt = 0;
        while (offset < txt.length()){
            Character c = txt.charAt(offset);
            switch (c){
                case '(':
                    if (offset + 1 < txt.length() && txt.charAt(offset+1)=='*') cpt++;
                    break;
                case '*':
                    if (cpt > 0) {
                        if (offset + 1 < txt.length() && txt.charAt(offset + 1) == ')') cpt--;
                    } else {
                        while (offset + 1 < txt.length() && txt.charAt(offset+1)=='*') offset++;
                        nextOffset = offset+1;
                        return document.getText(new TextRange(startOffset, offset+1)).replace('\n', ' ').trim();
                    }
                    break;
                case '-':
                    if (cpt <= 0) {
                        while (offset + 1 < txt.length() && txt.charAt(offset + 1) == '-') offset++;
                        nextOffset = offset+1;
                        return document.getText(new TextRange(startOffset, offset+1)).replace('\n', ' ').trim();
                    }
                    break;
                case '+':
                    if (cpt <= 0) {
                        while (offset + 1 < txt.length() && txt.charAt(offset + 1) == '+') offset++;
                        nextOffset = offset+1;
                        return document.getText(new TextRange(startOffset, offset+1)).replace('\n', ' ').trim();
                    }
                    break;
                default:
                    if (!Character.isWhitespace(c) && cpt <=0) return nextCommand(startOffset);
            }
            offset++;
        }
        return null;
    }

    private static boolean isCommand (String str){
        int cpt = 0;
        for (int i =0; i < str.length() - 1; i++){
            if (str.charAt(i) == '(' && str.charAt(i+1) == '*') cpt++;
            if (str.charAt(i) == '*' && str.charAt(i+1) == ')') cpt--;
        }
        return (cpt <= 0);
    }

    public int getOffset(){
        return nextOffset;
    }
}
