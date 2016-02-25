package org.univorleans.coq.util;

import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dabrowski on 24/01/2016.
 */
public class FilesUtil {

    public static List<VirtualFile> getSubdirs(VirtualFile file) {

        List<VirtualFile> dirs = new ArrayList<>();

        if (file.isDirectory()) {
            dirs.add(file);

            VirtualFile[] files = file.getChildren();


            for (VirtualFile f : files) {
                dirs.addAll(getSubdirs(f));

//                if (f.isDirectory()) {
//                    dirs.add(f);
//                    dirs.addAll(getSubdirs(f));
//                }
            }
        }
        return dirs;
    }

    public static VirtualFile[] getIncludes(CompileScope compileScope) {

        Module[] modules = compileScope.getAffectedModules();
        List<VirtualFile> includes = new ArrayList<>();
        for (Module module : modules) {
            ModuleRootManager root = ModuleRootManager.getInstance(module);
            for (VirtualFile virtualFile : root.getSourceRoots()) {
                includes.addAll(FilesUtil.getSubdirs(virtualFile).stream().collect(Collectors.toList()));
            }
        }
        return includes.toArray(new VirtualFile[0]);
    }

    public static Module getModule(ModuleManager manager, VirtualFile file){
        Module currentModule = null;

        Module[] modules = manager.getModules();
        for (Module module : modules) {
            if (module.getModuleScope().accept(file)){
                currentModule = module;
                break;
            }
        }
        return currentModule;
    }

    public static File[] getSourceRoots(Module currentModule){

        ModuleRootManager root = ModuleRootManager.getInstance(currentModule);
        VirtualFile[] roots = root.getSourceRoots();
        List<File> include = new ArrayList<>();
        for (VirtualFile vfile : roots){
            for (VirtualFile vfile2 : FilesUtil.getSubdirs(vfile)) {
                Path p = Paths.get(vfile.getPath()).relativize(Paths.get(vfile2.getPath()));
                File f = Paths.get(currentModule.getProject().getBasePath()+"/out/"+"production/"+currentModule.getName()).resolve(p).toFile();
                include.add(f);
            }
        }

        return include.toArray(new File[0]);
    }
}
