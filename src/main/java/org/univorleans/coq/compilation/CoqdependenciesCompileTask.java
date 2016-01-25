package org.univorleans.coq.compilation;

import com.intellij.compiler.server.BuildManager;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileTask;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SystemProperties;
import com.intellij.util.xmlb.SkipDefaultValuesSerializationFilters;
import com.intellij.util.xmlb.XmlSerializationException;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.univorleans.coq.files.CoqLanguageFileType;
import org.univorleans.coq.jps.builder.CoqBuilderUtil;
import org.univorleans.coq.jps.builder.CoqFileDependencies;
import org.univorleans.coq.jps.builder.CoqProjectDependencies;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dabrowski on 22/01/2016.
 */
public class CoqdependenciesCompileTask implements CompileTask {

    private static final Logger LOG = Logger.getInstance(CoqdependenciesCompileTask.class);

    /**
     * Computes project dependencies and store them as XML file
     * @param compileContext the compilation context
     * @return true if succeed
     */
    @Override
    public boolean execute(@NotNull CompileContext compileContext) {

        CoqProjectDependencies dependencies = computeDependencies(compileContext);
        if (dependencies == null) return false;
        return writeDependencies(compileContext, dependencies);
    }

    /**
     * Compute project dependencies
     * @param compileContext the compilation context
     * @return a CoqProjectDependencies object if succeeds, null otherwise
     */
    @Nullable
    private CoqProjectDependencies computeDependencies(@NotNull CompileContext compileContext) {

        compileContext.addMessage(CompilerMessageCategory.INFORMATION,
                "Computing dependencies",
                null, -1, -1);

        Project project = compileContext.getProject();
        Module[] modules = compileContext.getCompileScope().getAffectedModules();
        VirtualFile[] virtualFiles = compileContext.getCompileScope().getFiles(CoqLanguageFileType.INSTANCE, true);

        List<VirtualFile> sourceRoots = new ArrayList<>();
        for (Module module : modules){
            sourceRoots.addAll(Arrays.asList(compileContext.getSourceRoots(module)));
        }

        //VirtualFile base = project.getBaseDir().findChild("src");

        //VirtualFile[] subdirs = FilesUtil.getSubdirs(base);

        Sdk projectSdk = ProjectRootManager.getInstance(compileContext.getProject()).getProjectSdk();

        CoqdepWrapper coqdepWrapper = new CoqdepWrapper(compileContext, projectSdk, virtualFiles, sourceRoots);

        List <CoqFileDependencies> result = coqdepWrapper.execute();

        if (result == null) return null;
        return new CoqProjectDependencies(result);
    }

    private boolean writeDependencies(@NotNull CompileContext compileContext,
                                        @NotNull CoqProjectDependencies projectBuildOrder) {
        try {
            Project project = compileContext.getProject();
            File projectSystemDirectory = BuildManager.getInstance().getProjectSystemDirectory(project);
            LOG.debug("Serialize builder order");
            Document serializedDocument =
                    new Document(XmlSerializer.serialize(projectBuildOrder, new SkipDefaultValuesSerializationFilters()));

            File parentDir = new File(projectSystemDirectory, CoqBuilderUtil.BUILDER_DIRECTORY);

            if (!parentDir.exists()) parentDir.mkdirs();

            File file = new File(parentDir, CoqBuilderUtil.BUILD_ORDER_FILE_NAME);

            LOG.debug("Write builder order to " + file.getAbsolutePath());
            JDOMUtil.writeDocument(serializedDocument, file, SystemProperties.getLineSeparator());
            System.out.println("Write builder order to " + file.getAbsolutePath() + "(Done)");
            return true;
        }
        catch (XmlSerializationException e) {
            LOG.error("Can't serialize builder order object.", e);
            addPrepareDependenciesFailedMessage(compileContext);
            return false;
        }
        catch (IOException e) {
            LOG.warn("Some I/O errors occurred while writing builder orders to file", e);
            addPrepareDependenciesFailedMessage(compileContext);
            return false;
        }
    }

    private void addPrepareDependenciesFailedMessage(@NotNull CompileContext context) {
        context.addMessage(CompilerMessageCategory.ERROR, "Failed to submit dependencies info to compiler.", null, -1, -1);
    }

}
