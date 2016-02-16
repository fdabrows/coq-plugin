package org.univorleans.coq.coqdep;

import com.intellij.compiler.server.BuildManager;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileTask;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.util.SystemProperties;
import com.intellij.util.xmlb.SkipDefaultValuesSerializationFilters;
import com.intellij.util.xmlb.XmlSerializationException;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.univorleans.coq.jps.builder.CoqBuilderUtil;
import org.univorleans.coq.jps.builder.CoqProjectDependencies;
import org.univorleans.coq.util.CompilerMsg;

import java.io.File;
import java.io.IOException;

/**
 * Created by dabrowski on 22/01/2016.
 */
public class CoqdependenciesCompileTask implements CompileTask {

    private static final Logger LOG = Logger.getInstance(CoqdependenciesCompileTask.class);

    /**
     * Computes project coqdep and store them as XML file
     * @param compileContext the coqdep context
     * @return true if succeed
     */
    @Override
    public boolean execute(@NotNull CompileContext compileContext) {

        CoqProjectDependencies dependencies = computeDependencies(compileContext);
        if (dependencies == null) return false;
        return writeDependencies(compileContext, dependencies);
    }

    /**
     * Compute project coqdep
     * @param compileContext the coqdep context
     * @return a CoqProjectDependencies object if succeeds, null otherwise
     */
    @Nullable
    private CoqProjectDependencies computeDependencies(@NotNull CompileContext compileContext) {

        //CompilerMsg.info(compileContext, "Computing coqdep");
        return new CoqdepWrapper(compileContext).execute();

    }

    private boolean writeDependencies(@NotNull CompileContext compileContext,
                                        @NotNull CoqProjectDependencies projectBuildOrder) {
        try {

            Document serializedDocument =
                    new Document(XmlSerializer.serialize(projectBuildOrder,
                            new SkipDefaultValuesSerializationFilters()));


            Project project = compileContext.getProject();
            File projectSystemDirectory = BuildManager.getInstance().getProjectSystemDirectory(project);
            File parentDir = new File(projectSystemDirectory, CoqBuilderUtil.BUILDER_DIRECTORY);
            if (!parentDir.exists()) parentDir.mkdirs();
            File file = new File(parentDir, CoqBuilderUtil.BUILD_ORDER_FILE_NAME);

            JDOMUtil.writeDocument(serializedDocument, file, SystemProperties.getLineSeparator());

            LOG.debug("Write builder order to " + file.getAbsolutePath());
            return true;
        }
        catch (XmlSerializationException e) {
            LOG.error("Can't serialize builder order object.", e);
            CompilerMsg.error(compileContext, "Failed to submit coqdep info to compiler.", e);
            return false;
        }
        catch (IOException e) {
            LOG.warn("Some I/O errors occurred while writing builder orders to file", e);
            CompilerMsg.error(compileContext, "Failed to submit coqdep info to compiler.", e);
            return false;
        }
    }

}
