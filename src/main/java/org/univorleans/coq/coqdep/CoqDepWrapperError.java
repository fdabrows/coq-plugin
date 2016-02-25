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

package org.univorleans.coq.coqdep;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.compiler.CompileContext;
import org.univorleans.coq.util.StringUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dabrowski on 19/02/2016.
 */
public class CoqDepWrapperError implements Runnable{


    private final String regex = "File \"([a-zA-Z0-9/]+.v)\", characters ([0-9]*)-([0-9]*):(.*)";
    private String str;
    private CompileContext context;

    public CoqDepWrapperError(List<String> error) {

        this.str = StringUtil.stringConcat(error, "\n").trim();
    }

    @Override
    public void run(){

        String url = null;
        int line = -1;
        int column = -1;
        String error = str.trim();

        Matcher matcher = Pattern.compile(regex).matcher(str);
        if (matcher.matches()) {

            url = "file://" + matcher.group(1);
            error = matcher.group(4);

            VirtualFile virtualFile = VirtualFileManager.getInstance().refreshAndFindFileByUrl(url);
            FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
            Document document = fileDocumentManager.getDocument(virtualFile);

            int offset = Integer.parseInt(matcher.group(2));
            line = document.getLineNumber(offset) ;
            column = offset - document.getLineStartOffset(line);
        }

        String msg = str;
        if (url != null) msg = "in file " + url + ", " + error;

        context.addMessage(CompilerMessageCategory.ERROR, msg, url, line + 1, column + 1);
    }

    public void show(CompileContext context){
        this.context = context;
        ApplicationManager.getApplication().runReadAction(this);
    }
}
