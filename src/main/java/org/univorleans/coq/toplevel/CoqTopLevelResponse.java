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

package org.univorleans.coq.toplevel;

import org.univorleans.coq.errors.InvalidCoqtopResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CoqTopLevelResponse {

    public static final Pattern p_prompt =
            Pattern.compile("<prompt>([a-zA-Z0-9_]+)\\s<\\s([0-9]+)\\s\\|([a-zA-Z0-9_]+)?\\|\\s([0-9]+)\\s<\\s</prompt>");

    public final String preprompt;
    public final CoqTopLevelPrompt prompt;
    public final List<String> message;


    public CoqTopLevelResponse(String preprompt, CoqTopLevelPrompt prompt, List<String> message) throws InvalidCoqtopResponse {
        this.preprompt = preprompt;
        this.prompt = prompt;
        if (message != null) this.message = message;
        else this.message = new ArrayList<>();
    }

    public String message(){
        String msg = "";
        if (message == null) return msg;
        for (int i =0; i < message.size(); i++)
            msg += (message.get(i) + "\n");
        return msg;
    }

}
