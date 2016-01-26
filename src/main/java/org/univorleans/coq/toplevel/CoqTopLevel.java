/*
 * IntelliJ-coqplugin  / Plugin IntelliJ for Coq
 * Copyright (c) 2016
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

import org.univorleans.coq.errors.CoqtopPathError;
import org.univorleans.coq.errors.InvalidPrompt;
import org.univorleans.coq.errors.NoCoqProcess;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by dabrowski on 31/12/2015.
 */
public class CoqTopLevel {

    private Process p;
    private String proc;
    private String cmd;
    private BufferedReader processOutput;
    private BufferedWriter processInput;
    private BufferedReader processError;
    Project project;

    private static final Pattern p_prompt =
            Pattern.compile("<prompt>([a-zA-Z0-9_\n\r]+)\\s<\\s([0-9]+)\\s\\|([a-zA-Z0-9_]+)?\\|\\s([0-9]+)\\s<\\s</prompt>");

    private CoqTopLevel(Editor editor){
        project = editor.getProject();
    }

    public static CoqTopLevel getCoqTopLevel(Editor editor) throws CoqtopPathError {

        return new CoqTopLevel(editor);
    }

    public String readPrePrompt() throws IOException {
        return processError.readLine();
    }

    public String readPrompt() throws IOException, InvalidPrompt {

        StringBuilder builder = new StringBuilder();
        while (processError.ready()) {
            char c = (char) (processError.read());
            builder.append(c);
            if (c == '>') {
                String str = builder.toString();
                if (p_prompt.matcher(str).matches()) return str;
            }
        }
        throw new InvalidPrompt(builder.toString());
    }

    public List<String> readMessage() throws IOException {
        List<String> messageList = new ArrayList<>();
        messageList.add(processOutput.readLine());
        while (processOutput.ready()) {
            messageList.add(processOutput.readLine());
        }
        return messageList;
    }


    public CoqTopLevelResponse start() throws IOException, InvalidPrompt {

        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        // projectSdk null???
        VirtualFile base = project.getBaseDir().findChild("src");
        List<VirtualFile> subdirs = null;//CoqBaseCompiler.getSubdirs(base);
        List<String> cmd = new ArrayList<>();
        cmd.add(projectSdk.getHomePath() + "/bin/coqtop");
        cmd.add("-emacs");
        cmd.add("-I");
        cmd.add(project.getBasePath()+"/src");
        for (VirtualFile file : subdirs) {cmd.add("-I"); cmd.add(file.getPath());}

        File dir = new File(project.getBasePath()+"/src");

        String msg="-";
        for (String str : cmd)
            msg += " " + str;
        msg+="-";
        System.out.println(msg);

        Runtime runtime = Runtime.getRuntime();
        p = runtime.exec(cmd.toArray(new String[0]), null, dir);
        processOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        processInput = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        processError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        String prePrompt = readPrePrompt();
        CoqTopLevelPrompt prompt = new CoqTopLevelPrompt(readPrompt());
        CoqTopLevelMessage message = new CoqTopLevelMessage(readMessage());

        return new CoqTopLevelResponse(prePrompt, prompt, message);
    }

    public void stop() throws IOException {
            processOutput.close();
            processInput.close();
            p.destroy();
            processOutput = null;
            processInput = null;
            p = null;
    }

    public CoqTopLevelResponse send (String cmd) throws InvalidPrompt, IOException {

        String preprompt;
        CoqTopLevelPrompt prompt;
        CoqTopLevelMessage message;

        if (p == null) start();
            processInput.write(cmd + "\n");
            processInput.flush();
            preprompt = readPrePrompt();
            prompt = new CoqTopLevelPrompt(readPrompt());
            message = null;
            if (processOutput.ready()) message = new CoqTopLevelMessage(readMessage());
            return new CoqTopLevelResponse(preprompt, prompt, message);

    }

    public CoqTopLevelResponse backTo(int n) throws InvalidPrompt, IOException, NoCoqProcess {
        return send("BackTo "+n+".");
    }
}
