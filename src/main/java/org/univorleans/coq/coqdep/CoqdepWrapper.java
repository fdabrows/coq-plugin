package org.univorleans.coq.coqdep;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.files.CoqFileType;
import org.univorleans.coq.jps.builder.CoqProjectDependencies;
import org.univorleans.coq.jps.model.JpsCoqSdkType;
import org.univorleans.coq.util.FilesUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dabrowski on 24/01/2016.
 */
public class CoqdepWrapper {

    String[] cmd;
    CompileContext context;

    public CoqdepWrapper(CompileContext context) {

        // TODO : Prend le SDK du projet et pas celui du module
        Sdk projectSdk = ProjectRootManager.getInstance(context.getProject()).getProjectSdk();

        CompileScope compileScope = context.getCompileScope();

        Module[] modules = compileScope.getAffectedModules();
        VirtualFile[] virtualFiles = compileScope.getFiles(CoqFileType.INSTANCE, true);

        List<VirtualFile> sourceRoots = new ArrayList<>();
        for (Module module : modules) {
            ModuleRootManager root = ModuleRootManager.getInstance(module);
            sourceRoots.addAll(Arrays.asList(root.getSourceRoots()));
        }

        List<String> cmd = new ArrayList<>();
        File coqdep = JpsCoqSdkType.getDependenciesExecutable(projectSdk.getHomePath());

        cmd.add(coqdep.getPath());
        for (VirtualFile virtualFile : sourceRoots) {
            //cmd.add("-I");
            //cmd.add(virtualFile.getPath());
            for (VirtualFile subdir : FilesUtil.getSubdirs(virtualFile)) {
                cmd.add("-I");
                cmd.add(subdir.getPath());
            }
        }
        for (VirtualFile file : virtualFiles) cmd.add(file.getPath());


        this.cmd = cmd.toArray(new String[0]);
        this.context = context;
    }

    @Nullable
    public CoqProjectDependencies execute() {
        Runtime runtime = Runtime.getRuntime();
        BufferedReader processOutput;
        BufferedReader processError;
        Process coqdep;
        try {
            context.addMessage(CompilerMessageCategory.INFORMATION, "Computing dependencies", null, 0, 0);

            String msg = "";
            for (String str : cmd) msg+=str+" ";
            System.out.println(msg);
            context.addMessage(CompilerMessageCategory.INFORMATION, msg, null, 0, 0);


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
                    else context.addMessage(CompilerMessageCategory.ERROR, "Failed to submit coqdep info to compiler.\n" + str, null, 0, 0);
                }
                return null;
            }
            List<String> lines = new ArrayList<>();
            String str;
            while ((str = processOutput.readLine()) != null)
                lines.add(str);

            System.out.println(lines.size());
            // TODO : not use virtualFiles, prefer take information into lines
            CoqProjectDependencies dependencies = CoqdependenciesUtil.extractDependencies(lines);
            context.addMessage(CompilerMessageCategory.INFORMATION, "Computing dependencies...done", null, 0, 0);
            return dependencies;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
