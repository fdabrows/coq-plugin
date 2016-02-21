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
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.jps.builder.CoqProjectDependencies;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dabrowski on 24/01/2016.
 */
public class Interface implements CommandTask<CoqProjectDependencies, TaskError> {

    File coqdep;
    String[] cmd;
    File baseDir;
    String error = "";
    Project project;
    CoqProjectDependencies result;

    public Interface(Project project, File coqdep, VirtualFile baseDir, VirtualFile[] includes, VirtualFile[] files) {

this.project = project;
        this.coqdep = coqdep;
        this.baseDir = new File(baseDir.getPath());
        List<String> cmd = new ArrayList<>();

        cmd.add(coqdep.getPath());
        for (VirtualFile virtualFile : includes) {
            cmd.add("-I");
            cmd.add(virtualFile.getPath());
        }
/*
        Library[] libraries =
                LibraryTablesRegistrar.getInstance().getLibraryTable().getLibraries();
        for (Library lib : libraries){
            for (VirtualFile file : lib.getFiles(OrderRootType.SOURCES)){
                cmd.add("-I");
                cmd.add(file.getPath());
            }
        }*/

        for (VirtualFile file : files) {
            cmd.add(file.getPath());
        }

        this.cmd = cmd.toArray(new String[0]);
    }

    @Nullable
    @Override
    public boolean execute(){

        //if (!coqdep.canExecute()) throw new IOException();

        Runtime runtime = Runtime.getRuntime();
        BufferedReader processOutput;
        BufferedReader processError;
        Process coqdep;
        try {

            coqdep = runtime.exec(cmd, new String[0], baseDir);

            processOutput = new BufferedReader(new InputStreamReader(coqdep.getInputStream()));
            processError = new BufferedReader(new InputStreamReader(coqdep.getErrorStream()));

            if (coqdep.waitFor() > 0) {
                String str;
                error = "";
                while ((str = processError.readLine()) != null) {
                    error += str + "\n";
                }
                return false;
            }

            List<String> lines = new ArrayList<>();
            String str;
            while ((str = processOutput.readLine()) != null)
                lines.add(str);

            CoqProjectDependencies dependencies = Util.extractDependencies(project, lines);
            result = dependencies;
            return true;
//            return dependencies;
        } catch (IOException e) {
            error = e.getMessage();
            return false;
        } catch (InterruptedException e) {
            error = e.getMessage();
            return false;
        }
    }

    public String toString() {
        String msg = "";
        for (String str : cmd) {
            msg += str + " ";
        }
        return msg;
    }

    @Override
    public CoqProjectDependencies getValue(){
        return result;
    }

    @Override
    public TaskError getError(){
        return new ErrorMessage(error);
    }

}
