/*
 * IntelliJ-coqplugin  / Plugin IntelliJ for Coq
 * Copyright (c) 2016 F.Dabrowski
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

package org.univorleans.coq.coqtop;

import java.io.*;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import org.univorleans.coq.coqtop.errors.InvalidCoqtopResponse;
import org.univorleans.coq.util.ProcessChannels;

/**
 * An interface to coqtop processes
 * @author F. Dabrowski
 * @version 1.0
 */

public class Interface {

    private Project project;
    private File coqtop;
    private File base;
    private File[] include;
    private Process process;
    private ProcessChannels processChannels;

    /**
     *
     * @param coqtop : coqtop executable file
     * @param base : executing directory
     * @param include : include directories */

    public Interface(Project project, File coqtop, File base, File[] include){

        this.project = project;
        this.coqtop = coqtop;
        this.base = base;
        this.include = include;
    }

    /**
     * Starts coqtop process, should be called at most once
     * @return the initial response of coqtop
     * @throws IOException : if communication with coqtop fails
     * @throws InvalidCoqtopResponse : if receives a ill-formed response
     */
    @NotNull
    public Response start() throws IOException, InvalidCoqtopResponse {

        if (process != null) throw new IllegalStateException();
        process = Runtime.getRuntime().exec(Util.makeCommand(project, coqtop, include), null, base);
        processChannels = new ProcessChannels(process);
        return Util.readResponse(processChannels);
    }

    /**
     * Stop coqtop process
     * @throws IOException : if communication with coqtop fails
     */
    public void stop() throws IOException {

        if (process == null) throw new IllegalStateException();
        processChannels.close();
        process.destroy();
    }

    /**
     * Send a command to coqtop process
     * @param cmd : the command to send
     * @return : the response of coqtop
     * @throws InvalidCoqtopResponse : if receives a ill-formed response
     * @throws IOException : if communication with coqtop fails
     */
    @NotNull
    public Response send (@NotNull String cmd) throws InvalidCoqtopResponse, IOException {

        if (process == null) throw new IllegalStateException();
        processChannels.input.write(cmd + "\n");
        processChannels.input.flush();
        return Util.readResponse(processChannels);
    }

    public String toString(){
        String msg ="";
        for (String str : Util.makeCommand(project, coqtop, include))
            msg += str + " ";
        return msg;
    }
}
