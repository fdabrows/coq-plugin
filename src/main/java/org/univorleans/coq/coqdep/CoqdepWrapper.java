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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.univorleans.coq.jps.builder.CoqProjectDependencies;
import org.univorleans.coq.util.ExternalTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dabrowski on 24/01/2016.
 */
public class CoqdepWrapper extends ExternalTask<CoqProjectDependencies, CoqDepWrapperError > {

    Project project;
    File coqdep;
    VirtualFile[] includes;
    VirtualFile[] files;
    File baseDir;

    public CoqdepWrapper(Project project, File coqdep, VirtualFile[] includes, VirtualFile[] files, VirtualFile baseDir) {

        this.project = project;
        this.coqdep = coqdep;
        this.includes = includes;
        this.files = files;
        this.baseDir = new File(baseDir.getPath());
    }

    @Override
    public String[] command() {

        List<String> cmd = new ArrayList<>();

        cmd.add(coqdep.getPath());
        for (VirtualFile virtualFile : includes) {
            cmd.add("-I");
            cmd.add(virtualFile.getPath());
        }
        for (VirtualFile file : files) cmd.add(file.getPath());

        return cmd.toArray(new String[0]);
    }

    @Override
    public File baseDir() {
        return baseDir;
    }

    @Override
    public CoqProjectDependencies parseValue(List<String> list) {

        return CoqProjectDependencies.buildDependencies(list);
    }

    @Override
    public CoqDepWrapperError parseError(List<String> list) {

        return new CoqDepWrapperError(list);
    }

}