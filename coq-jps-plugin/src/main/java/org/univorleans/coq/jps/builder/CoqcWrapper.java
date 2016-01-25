package org.univorleans.coq.jps.builder;

import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.univorleans.coq.jps.model.JpsCoqSdkProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dabrowski on 25/01/2016.
 */
public class CoqcWrapper  {

    CompileContext compileContext;
    String[] cmd;
    File dir;
    JpsSdk<JpsSimpleElement<JpsCoqSdkProperties>> sdk;
    File[] includes;

    public CoqcWrapper(CompileContext context, JpsSdk<JpsSimpleElement<JpsCoqSdkProperties>> projectSdk, File[] includes, File dir) {

        //List<String> cmd = new ArrayList<>();
        //cmd.add(projectSdk.getHomePath() + "/bin/coqc");
        //cmd.add("-I");
        //cmd.add(dir.getPath());
        //for (File file : includes) {cmd.add("-I"); cmd.add(file.getPath());}
        //this.cmd = cmd.toArray(new String[0]);
        this.dir = dir;
        this.compileContext = context;
        this.sdk = projectSdk;
        this.includes = includes;
    }

    public boolean compile(File path){

        Runtime runtime = Runtime.getRuntime();
        BufferedReader processError;
        Process coqdep;
        try {

            List<String> cmd = new ArrayList<>();
            cmd.add(sdk.getHomePath() + "/bin/coqc");

            for (File file : includes) {cmd.add("-I"); cmd.add(file.getPath());}
            cmd.add("-I");
            cmd.add(dir.getPath());
            cmd.add(path.getPath());
            this.cmd = cmd.toArray(new String[0]);

            compileContext.processMessage(new CompilerMessage("coqc", BuildMessage.Kind.INFO,
                    commandToString()));

            coqdep = runtime.exec(this.cmd, new String[0], dir);
            processError = new BufferedReader(new InputStreamReader(coqdep.getErrorStream()));
            if (coqdep.waitFor() > 0)
                return false;
            List<String> lines = new ArrayList<>();
            String str;
            while ((str = processError.readLine()) != null){
                lines.add(str);
            compileContext.processMessage(new CompilerMessage("coqc", BuildMessage.Kind.INFO,
                    str));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String commandToString(){
        String msg="";
        for (String str : this.cmd){
            msg +=str + " ";
        }
        return msg;
    }
}
