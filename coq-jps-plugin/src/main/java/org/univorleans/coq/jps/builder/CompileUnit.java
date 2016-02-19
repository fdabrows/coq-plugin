package org.univorleans.coq.jps.builder;

import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.jps.builders.BuildRootDescriptor;
import org.jetbrains.jps.incremental.ModuleBuildTarget;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by dabrowski on 18/02/2016.
 */
public class CompileUnit {

    final ModuleBuildTarget target;
    final File file;
    final BuildRootDescriptor descriptor;

    public CompileUnit(ModuleBuildTarget target, File file, BuildRootDescriptor descriptor){
        this.target = target;
        this.file = file;
        this.descriptor = descriptor;
    }

    public String getInputName(){

        return FileUtil.getNameWithoutExtension(file.getPath());
    }

    public String getOutputName() {

        Path name = descriptor.getRootFile().toPath().relativize(file.toPath());
        Path newfile = target.getOutputDir().toPath().resolve(name);
        return FileUtil.getNameWithoutExtension(newfile.toString());

    }

}
