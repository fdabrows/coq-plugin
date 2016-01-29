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

package org.univorleans.coq.toplevel;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.univorleans.coq.errors.InvalidCoqtopResponse;
import org.univorleans.coq.util.FilesUtil;
import org.univorleans.coq.util.ProcessChannels;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dabrowski on 27/01/2016.
 */
public class CoqtopUtil {

    /** all elements of sourceRoots must be non null */
    @NotNull
    public static String[] makeCommand(@NotNull File coqtop, @NotNull File[] sourceRoots){

        List<String> cmd = new ArrayList<>();
        cmd.add(coqtop.getPath());
        cmd.add("-emacs");
        for (File file : sourceRoots) {
            cmd.add("-I"); cmd.add(file.getPath());
        }
        return cmd.toArray(new String[0]);
    }

    @NotNull
    public static String readPrePrompt(@NotNull ProcessChannels processChannels) throws IOException {
        return processChannels.error.readLine();
    }

    @NotNull
    public static String readPrompt(@NotNull  ProcessChannels processChannels) throws IOException, InvalidCoqtopResponse {

        StringBuilder builder = new StringBuilder();
        while (processChannels.error.ready()) {
            char c = (char) (processChannels.error.read());
            builder.append(c);
            if (c == '>') {
                String str = builder.toString();
                if (CoqtopResponse.p_prompt.matcher(str).matches()) return str;
            }
        }
        throw new InvalidCoqtopResponse(builder.toString());
    }

    @NotNull
    public static List<String> readMessage(@NotNull ProcessChannels processChannels) throws IOException {

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
    public static CoqtopResponse readResponse(@NotNull ProcessChannels processChannels) throws IOException, InvalidCoqtopResponse {

        String prePrompt = readPrePrompt(processChannels);
        CoqtopPrompt prompt = CoqtopPrompt.makePrompt(readPrompt(processChannels));
        List<String> message = CoqtopUtil.readMessage(processChannels);
        return new CoqtopResponse(prePrompt, prompt, message);
    }

    @NotNull
    public static String backTo(int n) throws InvalidCoqtopResponse, IOException {
        return "BackTo " + n + ".";
    }

    public static boolean isCommand (String str){
        int cpt = 0;
        for (int i =0; i < str.length() - 1; i++){
            if (str.charAt(i) == '(' && str.charAt(i+1) == '*') cpt++;
            if (str.charAt(i) == '*' && str.charAt(i+1) == ')') cpt--;
        }
        return (cpt == 0);
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

    public static File[] getSourceRoots(ModuleManager manager, VirtualFile file){

        Module currentModule = getModule(manager, file);

        ModuleRootManager root = ModuleRootManager.getInstance(currentModule);
        VirtualFile[] roots = root.getSourceRoots();
        List<File> include = new ArrayList<>();
        for (VirtualFile vfile : roots){
            include.add(new File(vfile.getPath()));
            for (VirtualFile vfile2 : FilesUtil.getSubdirs(vfile))
                include.add(new File(vfile2.getPath()));
        }

        return include.toArray(new File[0]);
    }
}
