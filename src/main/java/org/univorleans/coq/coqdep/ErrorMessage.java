package org.univorleans.coq.coqdep;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.compiler.CompileContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dabrowski on 19/02/2016.
 */
public class ErrorMessage implements TaskError {

    public String url;
    public int line;
    public int column;
    public String error;
    public String str;

    Pattern pattern = Pattern.compile(
            "File \"([a-zA-Z0-9/]+.v)\", characters ([0-9]*)-([0-9]*):(.*)");

    public ErrorMessage(String str) {

        this.str = str.trim();

    }

    public void show(CompileContext context) {

        ApplicationManager.getApplication().executeOnPooledThread(
                new Runnable() {

                    Runnable runnable = () -> {

                        Matcher matcher = pattern.matcher(str);
                        if (matcher.matches()) {
                            url = "file://" + matcher.group(1);
                            error = matcher.group(4);
                            int offset = Integer.parseInt(matcher.group(2));
                            VirtualFile virtualFile = VirtualFileManager.getInstance().refreshAndFindFileByUrl(url);
                            FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
                            Document document = fileDocumentManager.getDocument(virtualFile);
                            line = document.getLineNumber(offset);
                            column = offset - document.getLineStartOffset(line);
                        } else {
                            url = null;
                            line = -1;
                            column = -1;
                            error = str.trim();
                        }

                        String msg;
                        ;
                        if (url == null) {
                            msg = str;
                        } else {
                            msg = "in file " + url + ", " + error + " running coqdep";
                        }
                        context.addMessage(CompilerMessageCategory.ERROR,
                                msg, url, line + 1, column + 1);
//                        inter.getErrorMessage().show(compileContext);
                    };

                    public void run() {
                        ApplicationManager.getApplication().runReadAction(runnable);
                    }
                }
        );


    }
}
