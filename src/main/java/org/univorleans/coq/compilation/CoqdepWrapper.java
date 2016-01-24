package org.univorleans.coq.compilation;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dabrowski on 24/01/2016.
 */
public class CoqdepWrapper {

    String[] cmd;
    File dir;

    public CoqdepWrapper(CompileContext context, Sdk projectSdk, VirtualFile[] includes, VirtualFile[] files, VirtualFile dir) {

        List<String> cmd = new ArrayList<>();
        cmd.add(projectSdk.getHomePath() + "/bin/coqdep");
        cmd.add("-I");
        cmd.add(dir.getPath()+ File.separator + "src");
        for (VirtualFile file : includes) {cmd.add("-I"); cmd.add(file.getPath());}
        for (VirtualFile file : files) cmd.add(file.getPath());
        this.cmd = cmd.toArray(new String[0]);
        this.dir = new File (dir.getPath());

    }

    @Nullable
    public List<String> execute() {
        Runtime runtime = Runtime.getRuntime();
        BufferedReader processOutput;
        Process coqdep;
        try {
            coqdep = runtime.exec(cmd, new String[0], dir);
            processOutput = new BufferedReader(new InputStreamReader(coqdep.getInputStream()));
            if (coqdep.waitFor() > 0)
                return null;
            List<String> lines = new ArrayList<>();
            String str;
            while ((str = processOutput.readLine()) != null)
                lines.add(str);
            return lines;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
