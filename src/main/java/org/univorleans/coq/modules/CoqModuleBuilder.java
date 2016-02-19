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

package org.univorleans.coq.modules;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dabrowski on 20/01/2016.
 */
public class CoqModuleBuilder extends ModuleBuilder implements SourcePathsBuilder {

    private List<Pair<String,String>> mySourcePaths;
    private String myOutputPath;

    @Override
    public List<Pair<String, String>> getSourcePaths() throws ConfigurationException {

        if (mySourcePaths == null) {
            final List<Pair<String, String>> paths = new ArrayList<>();
            @NonNls final String path = getContentEntryPath() + File.separator + "src";
            new File(path).mkdirs();
            paths.add(Pair.create(path, ""));
            return paths;
        }
        return mySourcePaths;
    }

    @Override
    public void setSourcePaths(List<Pair<String, String>> sourcePaths) {

        mySourcePaths = sourcePaths != null? new ArrayList<>(sourcePaths) : null;
    }

    @Override
    public void addSourcePath(Pair<String, String> sourcePathInfo) {

        if (mySourcePaths == null) {
            mySourcePaths = new ArrayList<>();
        }
        mySourcePaths.add(sourcePathInfo);
    }

    public String getOutputPath() {

        if (myOutputPath == null){
            @NonNls final String path = getContentEntryPath() + File.separator + "out";
            new File(path).mkdir();
            return path;
        }
        return myOutputPath;
    }

    public void setOutputPath(String compilerOutputPath) {
        myOutputPath = acceptParameter(compilerOutputPath);
    }

    @Override
    public void setupRootModel(ModifiableRootModel modifiableRootModel) throws ConfigurationException {

        final CompilerModuleExtension compilerModuleExtension = modifiableRootModel.getModuleExtension(CompilerModuleExtension.class);
        compilerModuleExtension.setExcludeOutput(true);

        if (myJdk != null){
            modifiableRootModel.setSdk(myJdk);
        } else {
            modifiableRootModel.inheritSdk();
        }

        final String moduleRootPath = getContentEntryPath();
        if (moduleRootPath != null) {
            final LocalFileSystem lfs = LocalFileSystem.getInstance();
            VirtualFile moduleContentRoot = lfs.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(moduleRootPath));
            if (moduleContentRoot != null) {
                final ContentEntry contentEntry = modifiableRootModel.addContentEntry(moduleContentRoot);
                final List<Pair<String,String>> sourcePaths = getSourcePaths();
                if (sourcePaths != null) {
                    for (final Pair<String, String> sourcePath : sourcePaths) {
                        final VirtualFile sourceRoot = lfs.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(sourcePath.first));
                        if (sourceRoot != null) {
                            contentEntry.addSourceFolder(sourceRoot, false, sourcePath.second);
                        }
                    }
                }
            }
        }

        if (getOutputPath() != null) {
            // should set only absolute paths
            String canonicalPath;
            try {
                canonicalPath = FileUtil.resolveShortWindowsName(getOutputPath());
            }
            catch (IOException e) {
                canonicalPath = getOutputPath();
            }
            compilerModuleExtension
                    .setCompilerOutputPath(VfsUtil.pathToUrl(FileUtil.toSystemIndependentName(canonicalPath)));
        }
        else {
            compilerModuleExtension.inheritCompilerOutputPath(true);

        }
    }

    @Override
    public ModuleType getModuleType() {

        return CoqModuleType.getInstance();
    }

}
