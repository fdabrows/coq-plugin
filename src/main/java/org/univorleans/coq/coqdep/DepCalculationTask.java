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

package org.univorleans.coq.coqdep;

import com.intellij.compiler.server.BuildManager;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompileTask;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import org.univorleans.coq.files.VFileType;
import org.univorleans.coq.jps.builder.CoqBuilderUtil;
import org.univorleans.coq.jps.builder.CoqProjectDependencies;
import org.univorleans.coq.jps.model.JpsCoqSdkType;
import org.univorleans.coq.util.CompilerMsg;
import org.univorleans.coq.util.ExternalTask;
import org.univorleans.coq.util.FilesUtil;
import org.univorleans.coq.util.Messages;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * A task to be executed before compilation. Computes dependencies
 * as a CoqProjectDepencies stored on disk.
 * Created by dabrowski on 22/01/2016.
 */
public class DepCalculationTask implements CompileTask {

    private static final Logger LOG = Logger.getInstance(DepCalculationTask.class);

    /**
     * Computes project coqdep and store them as XML file
     *
     * @param compileContext the compile context
     * @return true if execution succeeded, false otherwise. If the
     * task returns false a message is added to the compile context.
     */
    @Override
    public boolean execute(@NotNull CompileContext compileContext) {


        Project project = compileContext.getProject();
        CompileScope compileScope = compileContext.getCompileScope();

        LOG.info("[coqdep] start computing dependencies for project " + project.getName());

        Sdk projectSdk = ProjectRootManager.getInstance(compileContext.getProject()).getProjectSdk();

        if (projectSdk == null) {
            JOptionPane.showMessageDialog(null, Messages.get("nosdk"), "SDK", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        File coqdep = JpsCoqSdkType.getDependenciesExecutable(projectSdk.getHomePath());
        VirtualFile[] includeDirs = FilesUtil.getIncludes(compileScope);
        VirtualFile[] sourceFiles = compileScope.getFiles(VFileType.INSTANCE, true);
        VirtualFile baseDir = project.getBaseDir();

        ExternalTask<CoqProjectDependencies, CoqDepWrapperError> task =
                new CoqdepWrapper(project, coqdep, includeDirs, sourceFiles, baseDir);
        if (task == null) return false;

        LOG.info("[coqdep] execute command: " + task.toString());

        if (!task.execute()) {
            task.getError().show(compileContext);
            return false;
        }

        try {
            File projectSystemDirectory = BuildManager.getInstance().getProjectSystemDirectory(project);
            File parentDir = new File(projectSystemDirectory, CoqBuilderUtil.BUILDER_DIRECTORY);
            if (!parentDir.exists()) parentDir.mkdirs();
            File file = new File(parentDir, CoqBuilderUtil.BUILD_ORDER_FILE_NAME);

            CoqProjectDependencies.write(file, task.getValue());
            LOG.info("[coqdep] dependencies written to " + file.getPath());
            return true;
        } catch (IOException e) {
            CompilerMsg.error(compileContext, "coqdep : error storing dependencies on disk.", e);
            return false;
        }

    }
}
