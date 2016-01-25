package org.univorleans.coq.compilation;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.jps.builder.CoqFileDependencies;
import org.univorleans.coq.jps.model.JpsCoqSdkType;
import org.univorleans.coq.util.FilesUtil;

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
    CompileContext context;
    VirtualFile[] virtualFiles;

    public CoqdepWrapper(CompileContext context, Sdk projectSdk, VirtualFile[] virtualFiles, List<VirtualFile> dirs) {

        List<String> cmd = new ArrayList<>();
        File coqc = JpsCoqSdkType.getByteCodeCompilerExecutable(projectSdk.getHomePath());

        cmd.add(coqc.getPath());
        for (VirtualFile virtualFile : dirs) {
            for (VirtualFile subdir : FilesUtil.getSubdirs(virtualFile)) {
                cmd.add("-I");
                cmd.add(subdir.getPath());
            }
        }
        for (VirtualFile file : virtualFiles) cmd.add(file.getPath());

        this.cmd = cmd.toArray(new String[0]);
        this.context = context;
        this.virtualFiles = virtualFiles;
    }

    @Nullable
    public List<CoqFileDependencies> execute() {
        Runtime runtime = Runtime.getRuntime();
        BufferedReader processOutput;
        BufferedReader processError;
        Process coqdep;
        try {
            coqdep = runtime.exec(cmd, new String[0], new File(context.getProject().getBasePath()));
            processOutput = new BufferedReader(new InputStreamReader(coqdep.getInputStream()));
            processError = new BufferedReader(new InputStreamReader(coqdep.getErrorStream()));

            if (coqdep.waitFor() > 0){
                List<String> lines = new ArrayList<>();
                String str;
                while ((str = processError.readLine()) != null) {
                    lines.add(str);
                    if (str.startsWith("***"))
                        context.addMessage(CompilerMessageCategory.WARNING, str, null, 0, 0);
                    else context.addMessage(CompilerMessageCategory.ERROR, "Failed to submit dependencies info to compiler.\n" + str, null, 0, 0);
                }
                return null;
            }
            List<String> lines = new ArrayList<>();
            String str;
            while ((str = processOutput.readLine()) != null)
                lines.add(str);

            List <CoqFileDependencies> coqFileDependencies = new ArrayList<>();

            // TODO : not use virtualFiles, prefer take information into lines
            for (VirtualFile vf : virtualFiles)
                coqFileDependencies.add(CoqdependenciesUtil.extractDependency(vf.getPath(), lines));
            return coqFileDependencies;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
