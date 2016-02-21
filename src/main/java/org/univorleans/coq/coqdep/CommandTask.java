package org.univorleans.coq.coqdep;

import com.intellij.openapi.compiler.CompileContext;

/**
 * Created by dabrowski on 19/02/2016.
 */
public interface CommandTask<A, B> {

    public boolean execute();
    public A getValue();
    public TaskError getError();
}
