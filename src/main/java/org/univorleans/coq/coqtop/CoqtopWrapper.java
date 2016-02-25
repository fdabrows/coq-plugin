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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import org.univorleans.coq.coqtop.errors.InvalidCoqtopResponse;
import org.univorleans.coq.roots.libraries.LibraryProvider;
import org.univorleans.coq.util.ProcessChannels;

/**
 * An interface to coqtop processes
 *
 * @author F. Dabrowski
 * @version 1.0
 */

public class CoqtopWrapper {

    private Project project;
    private File coqtop;
    private File base;
    private File[] include;
    private Process process;
    private ProcessChannels processChannels;

    private static final Pattern p_prompt =
            Pattern.compile("<prompt>([a-zA-Z0-9_]+)\\s<\\s([0-9]+)\\s\\|(([a-zA-Z0-9_]+\\|)+|\\|)\\s([0-9]+)\\s<\\s</prompt>");


    /**
     * @param coqtop  : coqtop executable file
     * @param base    : executing directory
     * @param include : include directories
     */

    public CoqtopWrapper(Project project, File coqtop, File base, File[] include) {

        this.project = project;
        this.coqtop = coqtop;
        this.base = base;
        this.include = include;
    }

    /**
     * Starts coqtop process, should be called at most once
     *
     * @return the initial response of coqtop
     * @throws IOException           : if communication with coqtop fails
     * @throws InvalidCoqtopResponse : if receives a ill-formed response
     */
    @NotNull
    public Response start() throws IOException, InvalidCoqtopResponse {

        if (process != null) throw new IllegalStateException();
        process = Runtime.getRuntime().exec(makeCommand(project, coqtop, include), null, base);
        processChannels = new ProcessChannels(process);
        return readResponse(processChannels);
    }

    /**
     * Stop coqtop process
     *
     * @throws IOException : if communication with coqtop fails
     */
    public void stop() throws IOException {

        if (process == null) throw new IllegalStateException();
        processChannels.close();
        process.destroy();
    }

    /**
     * Send a command to coqtop process
     *
     * @param cmd : the command to send
     * @return : the response of coqtop
     * @throws InvalidCoqtopResponse : if receives a ill-formed response
     * @throws IOException           : if communication with coqtop fails
     */
    @NotNull
    public Response send(@NotNull String cmd) throws InvalidCoqtopResponse, IOException {

        if (process == null) throw new IllegalStateException();
        processChannels.input.write(cmd + "\n");
        processChannels.input.flush();
        return readResponse(processChannels);
    }

    public String toString() {
        String msg = "";
        for (String str : makeCommand(project, coqtop, include))
            msg += str + " ";
        return msg;
    }

    @NotNull
    private List<String> readPrePrompt(@NotNull ProcessChannels processChannels) throws IOException {
        List<String> list = new ArrayList<>();
        list.add(processChannels.error.readLine());
        while (processChannels.error.ready()) {
            processChannels.error.mark(1);
            char c = (char) processChannels.error.read();
            if (c == '<') {
                String msg = String.valueOf(c);
                while (processChannels.error.ready()) {
                    c = (char) processChannels.error.read();
                    msg += String.valueOf(c);
                    if (c == '>') break;
                }
                if (msg.equals("<prompt>")) {
                    processChannels.error.reset();
                    break;
                }
            }
            processChannels.error.reset();
            list.add(processChannels.error.readLine());
        }
        return list;
    }

    @NotNull
    private String readPrompt(@NotNull ProcessChannels processChannels) throws IOException, InvalidCoqtopResponse {

        StringBuilder builder = new StringBuilder();
        char c = (char) (processChannels.error.read());
        while (c != '<') c = (char) (processChannels.error.read());
        builder.append(c);
        while (processChannels.error.ready()) {
            c = (char) (processChannels.error.read());
            builder.append(c);
            if (c == '>') {
                String str = builder.toString();
                if (p_prompt.matcher(str).matches()) return str;
            }
        }
        throw new InvalidCoqtopResponse(builder.toString());
    }

    @NotNull
    private List<String> readMessage(@NotNull ProcessChannels processChannels) throws IOException {

        List<String> messageList = new ArrayList<>();
        if (processChannels.output.ready()) {
            messageList.add(processChannels.output.readLine());
            while (processChannels.output.ready()) {
                messageList.add(processChannels.output.readLine());
            }
        }
        return messageList;
    }

    @NotNull
    private Response readResponse(@NotNull ProcessChannels processChannels) throws IOException, InvalidCoqtopResponse {


        List<String> prePrompt = readPrePrompt(processChannels);
        String str = readPrompt(processChannels);
        Matcher m = p_prompt.matcher(str);
        int globalCounter, proofCounter;
        if (m.matches()) {
            globalCounter = Integer.valueOf(m.group(2));
            proofCounter = Integer.valueOf(m.group(5));
        } else throw new InvalidCoqtopResponse(str);
        List<String> message = readMessage(processChannels);


        return new Response(prePrompt, globalCounter, proofCounter, message);
    }

    /** all elements of sourceRoots must be non null */
    @NotNull
    public static String[] makeCommand(Project project, @NotNull File coqtop, @NotNull File[] sourceRoots){

        List<String> cmd = new ArrayList<>();
        cmd.add(coqtop.getPath());
        cmd.add("-emacs");
        for (File file : sourceRoots) {
            cmd.add("-I");
            cmd.add(file.getPath());
        }

        List<Library> libraries = LibraryProvider.getLibraries(project);
        for (Library lib : libraries) {
            for (VirtualFile file : lib.getFiles(OrderRootType.CLASSES)) {
                cmd.add("-R");
                cmd.add(file.getPath());
                cmd.add(lib.getName());
            }
        }

        return cmd.toArray(new String[0]);
    }
}
