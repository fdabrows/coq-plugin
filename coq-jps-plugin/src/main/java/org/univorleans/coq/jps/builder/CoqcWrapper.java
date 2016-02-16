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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import static java.nio.file.StandardCopyOption.*;
/**
 * Created by dabrowski on 25/01/2016.
 */
public class CoqcWrapper  {

    String[] cmd;
    File dir;
    JpsSdk<JpsSimpleElement<JpsCoqSdkProperties>> sdk;
    File[] includes;
    List<String> lines = new ArrayList<>();

    public CoqcWrapper(JpsSdk<JpsSimpleElement<JpsCoqSdkProperties>> projectSdk, File dir) {

        this.dir = dir;
        this.sdk = projectSdk;
        this.includes = CoqBuilderUtil.getSubdirs(dir);

    }

    public boolean compile(File path){

        Runtime runtime = Runtime.getRuntime();

        Process coqdep;
        try {

            List<String> cmd = new ArrayList<>();
            cmd.add(sdk.getHomePath() + "/bin/coqc");

            for (File file : includes) {cmd.add("-I"); cmd.add(file.getPath());}
            //cmd.add("-I");
            //cmd.add(dir.getPath());
            cmd.add(path.getPath());
            this.cmd = cmd.toArray(new String[0]);

            coqdep = runtime.exec(this.cmd, new String[0], dir);
            lines = new ArrayList<>();
            if (coqdep.waitFor() > 0) {
                BufferedReader processError = new BufferedReader(new InputStreamReader(coqdep.getInputStream()));
                String str;
                while ((str = processError.readLine()) != null){
                    lines.add(str);
                }
                return false;
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

    public String getErrorMessage(){
        String msg = "";
        for (String str : lines) msg += str + "\n";
        msg+= "\n";
        for (String str : cmd) msg += str + " ";
        msg+= "\nIncludes ";
        for (File str : includes) msg += str.getPath() + " ";
        return msg;
    }

}
