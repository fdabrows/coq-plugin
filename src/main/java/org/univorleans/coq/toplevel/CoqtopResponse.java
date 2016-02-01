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

public class CoqtopResponse {


    public final static int PROOFMODE = 1;
    public final static int GENERALMODE = 0;

    public static final Pattern p_prompt =
            Pattern.compile("<prompt>([a-zA-Z0-9_]+)\\s<\\s([0-9]+)\\s\\|([a-zA-Z0-9_]+)?\\|\\s([0-9]+)\\s<\\s</prompt>");

    public final List<String> preprompt;
    public final CoqtopPrompt prompt;
    public final List<String> message;

    String msg = "";
    String info = "";


    public CoqtopResponse(List<String> preprompt, CoqtopPrompt prompt, List<String> message) throws InvalidCoqtopResponse {
        this.prompt = prompt;

        if (preprompt != null) this.preprompt = preprompt;
        else this.preprompt = new ArrayList<>();
        if (preprompt != null)
            for (int i =0; i < preprompt.size(); i++)
                info += (preprompt.get(i) + "\n");

        if (message != null) this.message = message;
        else this.message = new ArrayList<>();
        if (message != null)
        for (int i =0; i < message.size(); i++)
                msg += (message.get(i) + "\n");
    }

    public String message(){

        return msg;
    }

    public String info(){

        return info;
    }

    public int mode(){
        if (prompt.getProofCounter() == 0) return GENERALMODE;
        else return PROOFMODE;
    }

    @Override
    public String toString(){
        return info + "</PR>\n" + prompt + "</P>\n" + message()+"</M>\n";
    }

}
