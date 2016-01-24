package org.univorleans.coq.compilation;

import com.intellij.compiler.server.BuildManager;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileTask;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.diagnostic.Logger;
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
import org.univorleans.coq.util.FilesUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dabrowski on 22/01/2016.
 */
public class CoqdepCompileTask implements CompileTask {

    private static final Logger LOG = Logger.getInstance(CoqdepCompileTask.class);


    @Override
    public boolean execute(@NotNull CompileContext compileContext) {

        List<CoqFileDependencies> dependencies = getDependencies(compileContext);

        if (dependencies == null) {
            addPrepareDependenciesFailedMessage(compileContext);
            return false;
        }
        CoqProjectDependencies coqProjectDependencies =
                new CoqProjectDependencies(getDependencies(compileContext));
        return writeDependencies(compileContext, coqProjectDependencies);
    }

    @NotNull
    private CoqFileDependencies extractDependency(@NotNull String path, @NotNull List<String> coqdepOutput){
        List<String> dep = new ArrayList<>();
        for (String str2 : coqdepOutput) {
            String[] parts = str2.split(":");
            String[] left = parts[0].split(" ");
            String[] right = parts[1].split(" ");
            Pattern pattern = Pattern.compile("[^.]*.vo");
            for (String a : left) {
                if (path.equals(a.substring(0, a.length() - 1))) {
                    for (String b : right) {
                        Matcher matcher2 = pattern.matcher(b);
                        if (matcher2.matches()){
                            dep.add(b.substring(0, b.length() - 1));
                        }
                    }
                }
            }
        }
        return new CoqFileDependencies(path, dep);
    }

    @Nullable
    private List<CoqFileDependencies> getDependencies(@NotNull CompileContext compileContext) {

        VirtualFile[] files = compileContext.getCompileScope().getFiles(CoqLanguageFileType.INSTANCE, true);
        Project project = compileContext.getProject();
        VirtualFile base = project.getBaseDir().findChild("src");
        VirtualFile[] subdirs = FilesUtil.getSubdirs(base);
        Sdk projectSdk = ProjectRootManager.getInstance(compileContext.getProject()).getProjectSdk();
        CoqdepWrapper coqdepWrapper = new CoqdepWrapper(compileContext, projectSdk, subdirs, files, base);
        List <String> result = coqdepWrapper.execute();
        if (result == null) {
            addPrepareDependenciesFailedMessage(compileContext);
            return null;
        }
        List <CoqFileDependencies> coqFileDependencies = new ArrayList<>();
        for (VirtualFile vf : files)
            coqFileDependencies.add(extractDependency(vf.getPath(), result));
        return coqFileDependencies;
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
