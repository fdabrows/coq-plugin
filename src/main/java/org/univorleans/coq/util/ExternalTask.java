package org.univorleans.coq.util;

import org.univorleans.coq.coqdep.CoqDepWrapperError;
import org.univorleans.coq.jps.builder.CoqProjectDependencies;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

/**
 * Created by dabrowski on 23/02/2016.
 */
public abstract class ExternalTask<A, B> {

    private List<String> error;
    private List<String> value;

    public boolean execute() {
        try {
            String[] cmd = command();

            Runtime runtime = Runtime.getRuntime();
            Process coqdep = runtime.exec(cmd, new String[0], baseDir());

            BufferedReader processOutput = new BufferedReader(new InputStreamReader(coqdep.getInputStream()));
            BufferedReader processError = new BufferedReader(new InputStreamReader(coqdep.getErrorStream()));

            if (coqdep.waitFor() > 0) {
                error = StringUtil.readAll(processError);
                return false;
            } else {
                value = StringUtil.readAll(processOutput);
                return true;
            }
        } catch (IOException e) {
            error = Collections.singletonList(e.getMessage());
            return false;
        } catch (InterruptedException e) {
            error = Collections.singletonList(e.getMessage());
            return false;
        }
    }

    public A getValue() {
        return parseValue(value);
    }

    public B getError(){
        return parseError(error);
    }

    @Override
    public String toString() {
        return StringUtil.stringConcat(command(), " ");
    }

    public abstract String[] command();
    public abstract File baseDir();
    public abstract A parseValue(List<String> str);
    public abstract B parseError(List<String> str);
}
