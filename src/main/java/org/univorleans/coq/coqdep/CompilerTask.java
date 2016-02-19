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
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompileTask;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.ui.configuration.ProjectStructureConfigurable;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.SystemProperties;
import com.intellij.util.xmlb.SkipDefaultValuesSerializationFilters;
import com.intellij.util.xmlb.XmlSerializer;
import org.jetbrains.annotations.NotNull;

import org.univorleans.coq.files.CoqFileType;
import org.univorleans.coq.jps.builder.CoqBuilder;
import org.univorleans.coq.jps.builder.CoqBuilderUtil;
import org.univorleans.coq.jps.builder.CoqProjectDependencies;
import org.univorleans.coq.jps.model.JpsCoqSdkType;
import org.univorleans.coq.util.CompilerMsg;
import org.univorleans.coq.util.FilesUtil;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A task to be executed before compilation. Computes dependencies
 * as a CoqProjectDepencies stored on disk.
 * Created by dabrowski on 22/01/2016.
 */
public class CompilerTask implements CompileTask {

    public static final Logger LOG = Logger.getInstance(CoqBuilder.class);

    /**
     * Computes project coqdep and store them as XML file
     *
     * @param compileContext the compile context
     * @return true if execution succeeded, false otherwise. If the
     * task returns false a message is added to the compile context.
     */
    @Override
    public boolean execute(@NotNull CompileContext compileContext) {

        LOG.info("[coqdep] start computing dependencies for project " + compileContext.getProject().getName());

        Project project = compileContext.getProject();
        CompileScope compileScope = compileContext.getCompileScope();

        VirtualFile baseDir = project.getBaseDir();
        VirtualFile[] includeDirs = getIncludes(compileScope);
        VirtualFile[] sourceFiles = compileScope.getFiles(CoqFileType.INSTANCE, true);

        Sdk projectSdk = ProjectRootManager.getInstance(compileContext.getProject()).getProjectSdk();

        if (projectSdk == null) {
            showSdkError();
            return false;
        }

        File coqdep = JpsCoqSdkType.getDependenciesExecutable(projectSdk.getHomePath());

        Interface inter = new Interface(coqdep, baseDir, includeDirs, sourceFiles);

        LOG.info("[coqdep] execute command: " + inter.toString());

        CoqProjectDependencies dependencies = inter.execute();

        if (dependencies == null) {
            inter.getErrorMessage().show(compileContext);
            return false;
        }

        try

        {
            org.jdom.Document serializedDocument =
                    new org.jdom.Document(XmlSerializer.serialize(dependencies,
                            new SkipDefaultValuesSerializationFilters()));

            File projectSystemDirectory = BuildManager.getInstance().getProjectSystemDirectory(project);
            File parentDir = new File(projectSystemDirectory, CoqBuilderUtil.BUILDER_DIRECTORY);
            if (!parentDir.exists()) parentDir.mkdirs();
            File file = new File(parentDir, CoqBuilderUtil.BUILD_ORDER_FILE_NAME);

            JDOMUtil.writeDocument(serializedDocument, file, SystemProperties.getLineSeparator());
            LOG.info("[coqdep] dependencies written to " + file.getPath());
            return true;
        } catch (
                IOException e
                )

        {
            CompilerMsg.error(compileContext, "coqdep : error storing dependencies on disk.", e);
            return false;
        }

    }

    private VirtualFile[] getIncludes(CompileScope compileScope) {

        Module[] modules = compileScope.getAffectedModules();
        List<VirtualFile> includes = new ArrayList<>();
        for (Module module : modules) {
            ModuleRootManager root = ModuleRootManager.getInstance(module);
            for (VirtualFile virtualFile : root.getSourceRoots()) {
                for (VirtualFile subdir : FilesUtil.getSubdirs(virtualFile)) {
                    includes.add(subdir);
                }
            }
        }
        return includes.toArray(new VirtualFile[0]);
    }

    private static void showSdkError() {
        String error_msg =
                "Cannot start coqdep: the sdk is not specified.\n" +
                        "Specifiy the sdk at Project Structure dialog";
        JOptionPane.showMessageDialog(null, error_msg, "SDK Error", JOptionPane.ERROR_MESSAGE);

    }

}
