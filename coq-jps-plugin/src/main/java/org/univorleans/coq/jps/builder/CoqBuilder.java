/*
 * IntelliJ-coqplugin  / Plugin IntelliJ for Coq
 * Copyright (c) 2016 F. Dabrowski
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.univorleans.coq.jps.builder;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.FileProcessor;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.incremental.*;
import org.jetbrains.jps.incremental.fs.CompilationRound;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.module.*;
import org.univorleans.coq.jps.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


/**
 * Created by dabrowski on 22/01/2016.
 */
public class CoqBuilder extends ModuleLevelBuilder {

    //TODO : remove this field
    public static String coqPath = null;

    private boolean additional_pass_required = false;

    Set<CompileUnit> toCompile = new HashSet<>();

    Set<File> includes = new HashSet<>();

    public CoqBuilder() {
        super(BuilderCategory.TRANSLATOR);
    }

    // We assume all dirty files and descendents are within the moduleChunk and
    // the module chunk contains at most one module
    @Override
    public ExitCode build(CompileContext compileContext, ModuleChunk moduleChunk,
                          DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> dirtyFilesHolder,
                          OutputConsumer outputConsumer) throws ProjectBuildException, IOException {

        Set<JpsModule> modules = moduleChunk.getModules();

        if (modules.size() == 0) {
            showInfo(compileContext, "Targets are up to date.");
            return ExitCode.NOTHING_DONE;
        }
        if (modules.size() > 1) {
            showError(compileContext, "Modules inter-dependencies is not supported yet!");
            return ExitCode.NOTHING_DONE;
        }

        CoqProjectDependencies dependencies =
                CoqBuilderUtil.readFromXML(compileContext,
                        CoqBuilderUtil.BUILD_ORDER_FILE_NAME,
                        CoqProjectDependencies.class);

        if (dependencies == null) {
            showError(compileContext, "Can't read dependencies!");
            return ExitCode.ABORT;
        }

        FileProcessor<JavaSourceRootDescriptor, ModuleBuildTarget> processor =
                new FileProcessor<JavaSourceRootDescriptor, ModuleBuildTarget>() {
                    @Override
                    public boolean apply(ModuleBuildTarget target, File file, JavaSourceRootDescriptor root) throws IOException {

                        toCompile.add(new CompileUnit(target, file, root));

                        Collection<File> list = CoqBuilderUtil.getSubdirs(root.getRootFile());
                        for (File f : list) {
                            Path p = root.getRootFile().toPath().relativize(f.toPath());
                            includes.add(target.getOutputDir().toPath().resolve(p).toFile());
                        }
                        //includes.addAll(CoqBuilderUtil.getSubdirs(root.getRootFile()));


                        List<String> deps = dependencies.getDependents(file.getPath());

                        for (String f : deps) {
                            if (!FSOperations.isMarkedDirty(compileContext, CompilationRound.CURRENT, new File(f))) {
                                FSOperations.markDirty(compileContext, CompilationRound.NEXT, new File(f));
                                additional_pass_required = true;
                            }
                        }
                        return true;
                    }
                };

        dirtyFilesHolder.processDirtyFiles(processor);

        if (additional_pass_required) {
            additional_pass_required = false;
            return ExitCode.ADDITIONAL_PASS_REQUIRED;
        }

        if (toCompile.size() == 0) {

            showInfo(compileContext, "Targets are up to date.");
            return ExitCode.NOTHING_DONE;
        }

        List<CompileUnit> orderedFiles = dependencies.getOrderedFiles(toCompile);

        // Get coqc command, put in some private method
        JpsModule module = modules.iterator().next();

        if (!(module.getModuleType() instanceof JpsCoqModuleType)) {
            showError(compileContext, "Module " + module.getName() + " is not a Coq Module!");
            return ExitCode.ABORT;
        }

        JpsSdk<JpsSimpleElement<JpsCoqSdkProperties>> sdk = module.getSdk(JpsCoqSdkType.INSTANCE);

        if (sdk == null) {
            showError(compileContext, "No SDK associated with module " + module.getName() + "!");
            return ExitCode.ABORT;
        }


        CoqcWrapper coqc = new CoqcWrapper(sdk, includes.toArray(new File[0]));


        // Compile dirty files
        String compiled = "";

        for (CompileUnit source : orderedFiles) {

            File f = source.file;
            if (!(coqc.compile(f))) {

                //TODO : parse error message
                ErrorMessage message = coqc.getErrorMessage();
                compileContext.processMessage(
                        new CompilerMessage("coqc", BuildMessage.Kind.ERROR, message.message, message.url, -1, -1, -1, message.line, message.column)
                );
                return ExitCode.ABORT;
            }


            //showInfo(compileContext, coqc.toString());

            File outvo = new File(source.getOutputName() + ".vo");
            File outglob = new File(source.getOutputName() + ".glob");
            File invo = new File(source.getInputName() + ".vo");
            File inglob = new File(source.getInputName() + ".glob");
            File outv = new File(source.getOutputName() + ".v");

            if (invo.exists()) {
                FileUtil.rename(invo, outvo);
            }
            if (inglob.exists()) {
                FileUtil.rename(inglob, outglob);
            }
            if (outv.exists()) {
                FileUtil.delete(outv);
            }
            // Remove .v from output
            // Synchronize structureview
            showInfo(compileContext, invo.getPath() + "->" + outvo.getPath());
            outputConsumer.registerOutputFile(moduleChunk.representativeTarget(), outvo, Collections.singleton(source.file.getPath()));
            outputConsumer.registerOutputFile(moduleChunk.representativeTarget(), outglob, Collections.singleton(source.file.getPath()));
            compiled += source.target.getOutputDir().toPath().relativize(outvo.toPath()) + " ";
        }
        //showInfo(compileContext, compiled);
        return ExitCode.OK;
    }

    private void showInfo(CompileContext context, String str) {
        context.processMessage(new CompilerMessage(
                "coqc", BuildMessage.Kind.INFO, str));
    }

    private void showError(CompileContext context, String str) {
        context.processMessage(new CompilerMessage(
                "coqc", BuildMessage.Kind.ERROR, str));
    }

    @Override
    public List<String> getCompilableFileExtensions() {

        return Collections.singletonList("v");
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Coq Builder";
    }
}