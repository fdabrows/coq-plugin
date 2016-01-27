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

import org.jetbrains.annotations.NotNull;
import org.univorleans.coq.errors.InvalidCoqtopResponse;

import java.io.File;
import java.io.IOException;
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
                if (CoqTopLevelResponse.p_prompt.matcher(str).matches()) return str;
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
    public static CoqTopLevelResponse readResponse(@NotNull ProcessChannels processChannels) throws IOException, InvalidCoqtopResponse {

        String prePrompt = readPrePrompt(processChannels);
        CoqTopLevelPrompt prompt = new CoqTopLevelPrompt(readPrompt(processChannels));
        List<String> message = CoqtopUtil.readMessage(processChannels);
        return new CoqTopLevelResponse(prePrompt, prompt, message);
    }

    @NotNull
    public static String backTo(int n) throws InvalidCoqtopResponse, IOException {
        return "BackTo " + n + ".";
    }
}
