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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.univorleans.coq.coqtop.errors.InvalidCoqtopResponse;
import org.univorleans.coq.roots.libraries.LibraryProvider;
import org.univorleans.coq.util.FilesUtil;
import org.univorleans.coq.util.ProcessChannels;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dabrowski on 27/01/2016.
 */
public class Util {

    private static final Pattern p_prompt =
            Pattern.compile("<prompt>([a-zA-Z0-9_]+)\\s<\\s([0-9]+)\\s\\|(([a-zA-Z0-9_]+\\|)+|\\|)\\s([0-9]+)\\s<\\s</prompt>");


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

    @NotNull
    private static List<String> readPrePrompt(@NotNull ProcessChannels processChannels) throws IOException {
        List <String> list = new ArrayList<>();
        list.add(processChannels.error.readLine());
        while (processChannels.error.ready()){
            processChannels.error.mark(1);
            char c = (char) processChannels.error.read();
            if (c == '<'){
                String msg = String.valueOf(c);
                while (processChannels.error.ready()){
                    c = (char) processChannels.error.read();
                    msg += String.valueOf(c);
                    if (c =='>') break;
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
    private static String readPrompt(@NotNull  ProcessChannels processChannels) throws IOException, InvalidCoqtopResponse {

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
    private static List<String> readMessage(@NotNull ProcessChannels processChannels) throws IOException {

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
    public static Response readResponse(@NotNull ProcessChannels processChannels) throws IOException, InvalidCoqtopResponse {



        List<String> prePrompt = readPrePrompt(processChannels);
        String str = readPrompt(processChannels);
        Matcher m = p_prompt.matcher(str);
        int globalCounter, proofCounter;
        if (m.matches()) {
            globalCounter = Integer.valueOf(m.group(2));
            proofCounter = Integer.valueOf(m.group(5));
        }
        else throw new InvalidCoqtopResponse(str);
        List<String> message = Util.readMessage(processChannels);


        return new Response(prePrompt, globalCounter, proofCounter, message);
    }

    @NotNull
    public static String backTo(int n) throws InvalidCoqtopResponse, IOException {
        return "BackTo " + n + ".";
    }

    public static Module getModule(ModuleManager manager, VirtualFile file){
        Module currentModule = null;

        Module[] modules = manager.getModules();
        for (Module module : modules) {
            if (module.getModuleScope().accept(file)){
                currentModule = module;
                break;
            }
        }
        return currentModule;
    }

    public static File[] getSourceRoots(Module currentModule){

        ModuleRootManager root = ModuleRootManager.getInstance(currentModule);
        VirtualFile[] roots = root.getSourceRoots();
        List<File> include = new ArrayList<>();
        for (VirtualFile vfile : roots){
            for (VirtualFile vfile2 : FilesUtil.getSubdirs(vfile)) {
                Path p = Paths.get(vfile.getPath()).relativize(Paths.get(vfile2.getPath()));
                File f = Paths.get(currentModule.getProject().getBasePath()+"/out/"+"production/"+currentModule.getName()).resolve(p).toFile();
                include.add(f);
            }
        }

        return include.toArray(new File[0]);
    }


}
