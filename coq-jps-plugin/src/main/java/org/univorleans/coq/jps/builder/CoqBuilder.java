package org.univorleans.coq.jps.builder;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.FileProcessor;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.incremental.*;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.java.JpsJavaExtensionService;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.module.*;
import org.univorleans.coq.jps.model.*;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Created by dabrowski on 22/01/2016.
 */
public class CoqBuilder extends ModuleLevelBuilder {

    public static String coqPath = null;
    public static final Logger LOG = Logger.getInstance(CoqBuilder.class);


    public CoqBuilder() {

        super(BuilderCategory.TRANSLATOR);
    }

    @Override
    public ExitCode build(CompileContext compileContext, ModuleChunk moduleChunk,
                          DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> dirtyFilesHolder,
                          OutputConsumer outputConsumer) throws ProjectBuildException, IOException {

        JpsModule[] modules = moduleChunk.getModules().toArray(new JpsModule[0]);
        if (modules.length == 0) return ExitCode.OK;

        JpsModuleType moduleType = modules[0].getModuleType();
        if (!(moduleType instanceof JpsCoqModuleType)) {
            showError(compileContext, "Not a Coq Module!");
            return ExitCode.ABORT;
        }

        JpsSdk<JpsSimpleElement<JpsCoqSdkProperties>> sdk =
                getSdk(modules[0]);
        if (sdk == null) {
            showError(compileContext, "No Sdk!");
            return ExitCode.ABORT;
        }

        CoqProjectDependencies dependencies =
                CoqBuilderUtil.readFromXML(compileContext,
                        CoqBuilderUtil.BUILD_ORDER_FILE_NAME,
                        CoqProjectDependencies.class);

        if (dependencies == null) {
            showError(compileContext, "Can't get dependencies!");
            return ExitCode.ABORT;
        }

        showInfo(compileContext, "has dirty files : " + String.valueOf(dirtyFilesHolder.hasDirtyFiles()));

        FileProcessor<JavaSourceRootDescriptor, ModuleBuildTarget> processor =
                new FileProcessor<JavaSourceRootDescriptor, ModuleBuildTarget>() {
                    @Override
                    public boolean apply(ModuleBuildTarget target, File file, JavaSourceRootDescriptor root) throws IOException {

                        File baseDir = root.getRootFile();
                        CoqcWrapper coqc = new CoqcWrapper(sdk, baseDir);

                        List<String> files = dependencies.getDependents(file.getPath());
                        String msg = "";
                        for (String f : files) msg+= f + " ";
                        showInfo(compileContext, "Order " + msg);

                        for (String source : files) {
                            File f = new File(source);
                            showInfo(compileContext, "Build " + f.getPath());
                            if (!(coqc.compile(f))) {
                                showError(compileContext, coqc.getErrorMessage());
                                return false;
                            }
                        }
                        return true;
                    }
                };

        dirtyFilesHolder.processDirtyFiles(processor);
        return ExitCode.OK;
    }

    private void showInfo(CompileContext context, String str) {
        context.processMessage(new CompilerMessage(
                "Builder", BuildMessage.Kind.INFO, str));
    }

    private void showError(CompileContext context, String str) {
        context.processMessage(new CompilerMessage(
                "coqc", BuildMessage.Kind.ERROR, str));
    }


    private boolean doBuild(CompileContext compileContext, ModuleChunk moduleChunk, JpsModule module, JpsSdk<JpsSimpleElement<JpsCoqSdkProperties>> sdk, OutputConsumer outputConsumer) {

        CoqProjectDependencies dependencies =
                CoqBuilderUtil.readFromXML(compileContext,
                        CoqBuilderUtil.BUILD_ORDER_FILE_NAME,
                        CoqProjectDependencies.class);

        if (dependencies == null) {
            compileContext.processMessage(new CompilerMessage("Coq", BuildMessage.Kind.ERROR, "No Dep"));
            return false;
        }

        // SourceRoots(0)
        File dir = module.getSourceRoots().get(0).getFile();
        File[] includes = CoqBuilderUtil.getSubdirs(module.getSourceRoots().get(0).getFile());

        CoqcWrapper coqc = new CoqcWrapper(sdk, dir);

        for (String str : dependencies.getOrderedFiles(dependencies.getAllFiles())) {

            // SourceRoots(0) for relative path
            File file = new File(str);

            coqc.compile(file);

            try {
                compileContext.processMessage(new CompilerMessage("Coq file", BuildMessage.Kind.INFO, getBuildOutputDirectory(module, false, compileContext).getPath()));
            } catch (ProjectBuildException e) {
                e.printStackTrace();
            }
            String name = FileUtil.getNameWithoutExtension(new File(str));
            File outvo = new File(name + ".vo");

            File outglob = new File(name + ".glob");
            try {
                outvo = new File(getBuildOutputDirectory(module, false, compileContext), name + ".vo");
                outglob = new File(getBuildOutputDirectory(module, false, compileContext), name + ".glob");
            } catch (ProjectBuildException e) {
                e.printStackTrace();
            }
            compileContext.processMessage(new CompilerMessage("Coq vo file", BuildMessage.Kind.INFO, outvo.getPath()));
            compileContext.processMessage(new CompilerMessage("Coq glob file", BuildMessage.Kind.INFO, outglob.getPath()));
            try {
                outputConsumer.registerOutputFile(moduleChunk.representativeTarget(), outvo, Collections.EMPTY_LIST);
                outputConsumer.registerOutputFile(moduleChunk.representativeTarget(), outglob, Collections.EMPTY_LIST);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //try {
            //File out = new File(getBuildOutputDirectory(module, false, compileContext), name + ".vo");
            //new File(str+"o").renameTo(out);
            //outputConsumer.registerOutputFile();
            //compileContext.processMessage(new CompilerMessage("Coq", BuildMessage.Kind.INFO, out.getPath()));

            //outputConsumer.registerOutputFile(moduleChunk.representativeTarget(), out, Collections.EMPTY_LIST);
            //} catch (ProjectBuildException e) {
            //    e.printStackTrace();
            //} //catch (IOException e) {
            // e.printStackTrace();
            //}

        }
        return true;
    }

    private JpsSdk<JpsSimpleElement<JpsCoqSdkProperties>> getSdk(JpsModule module) {
        return module.getSdk(JpsCoqSdkType.INSTANCE);
    }

    @NotNull
    private static File getBuildOutputDirectory(@NotNull JpsModule module,
                                                boolean forTests,
                                                @NotNull CompileContext context) throws ProjectBuildException {
        JpsJavaExtensionService instance = JpsJavaExtensionService.getInstance();
        File outputDirectory = instance.getOutputDirectory(module, forTests);
        if (outputDirectory == null) {
            String errorMessage = "No output dir for module " + module.getName();
            context.processMessage(new CompilerMessage("OUTPUT", BuildMessage.Kind.ERROR, errorMessage));
            throw new ProjectBuildException(errorMessage);
        }
        if (!outputDirectory.exists()) {
            FileUtil.createDirectory(outputDirectory);
        }
        return outputDirectory;
    }


    private String getContentRootPath(JpsModule module) {
        final List<String> urls = module.getContentRootsList().getUrls();
        if (urls.size() == 0) {
            throw new RuntimeException("Can't find content root in module");
        }
        String url = urls.get(0);
        return url.substring("file://".length());
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