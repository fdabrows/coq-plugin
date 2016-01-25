package org.univorleans.coq.jps.builder;

import com.intellij.ide.ui.AppearanceOptionsTopHitProvider;
import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.incremental.*;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.incremental.messages.ProgressMessage;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.module.*;
import org.univorleans.coq.jps.model.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.univorleans.coq.jps.builder.CoqBuilderUtil.LOG;

/**
 * Created by dabrowski on 22/01/2016.
 */
public class CoqBuilder extends ModuleLevelBuilder{

    public static String coqPath = null;

    public CoqBuilder() {

        super(BuilderCategory.TRANSLATOR);
    }

    @Override
    public ExitCode build(CompileContext compileContext, ModuleChunk moduleChunk, DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> dirtyFilesHolder, OutputConsumer outputConsumer) throws ProjectBuildException, IOException {

        for (JpsModule module : moduleChunk.getModules()){
            JpsModuleType moduleType = module.getModuleType();
            LOG.info("Module Type : " + moduleType.toString());
            if (!(moduleType instanceof JpsCoqModuleType)) continue;
            JpsSdk<JpsSimpleElement<JpsCoqSdkProperties>> sdk = this.getSdk(module);
            if (sdk == null) {
                compileContext.processMessage(new CompilerMessage(
                        "coq", BuildMessage.Kind.ERROR, "Can't find Coq SDK"));
                continue;
            }
            if (!doBuild(compileContext, module, sdk)) return ExitCode.ABORT;
        }
        return ExitCode.OK;
    }

    public static File[] getSubdirs(File file) {

        List<File> dirs = new ArrayList<>();
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File f : files) {
                dirs.addAll(Arrays.asList(getSubdirs(f)));

/*                if (f.isDirectory()) {
                    dirs.add(f);
                    dirs.addAll(Arrays.asList(getSubdirs(f)));
                }*/

            }
        }
        return dirs.toArray(new File[0]);
    }

    private boolean doBuild(CompileContext compileContext, JpsModule module, JpsSdk<JpsSimpleElement<JpsCoqSdkProperties>> sdk){
        compileContext.processMessage(new ProgressMessage("Coq build"));
        compileContext.processMessage(new CompilerMessage("Coq", BuildMessage.Kind.INFO, "Start build"));
        CoqProjectDependencies dependencies =
                CoqBuilderUtil.readFromXML(compileContext,
                        CoqBuilderUtil.BUILD_ORDER_FILE_NAME,
                        CoqProjectDependencies.class);
        File dir = module.getSourceRoots().get(0).getFile();
        File[] includes = getSubdirs(module.getSourceRoots().get(0).getFile());
        CoqcWrapper coqc = new CoqcWrapper(compileContext, sdk, includes, dir);
        for (String str : dependencies.getOrderedFiles(dependencies.getAllFiles())){
            coqc.compile(new File(str));
        }
        return true;
    }

    private JpsSdk<JpsSimpleElement<JpsCoqSdkProperties>> getSdk(JpsModule module) {
        return module.getSdk(JpsCoqSdkType.INSTANCE);
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