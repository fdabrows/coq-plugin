package org.univorleans.coq.util;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessageCategory;

/**
 * Created by dabrowski on 26/01/2016.
 */
public class CompilerMsg {

    public static void info(CompileContext compileContext, String msg) {

       compileContext.addMessage(CompilerMessageCategory.INFORMATION,
            msg, null,-1,-1);}

    public static void error(CompileContext compileContext, String msg) {

        compileContext.addMessage(CompilerMessageCategory.INFORMATION,
                msg, null,-1,-1);
    }

    public static void error(CompileContext compileContext, String msg, Exception e) {

        compileContext.addMessage(CompilerMessageCategory.INFORMATION,
                msg, null,-1,-1);
    }

}
