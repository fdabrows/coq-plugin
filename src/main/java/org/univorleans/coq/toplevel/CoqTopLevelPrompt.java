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

import org.univorleans.coq.errors.InvalidCoqtopResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoqTopLevelPrompt {

    private static final Pattern p_prompt =
            Pattern.compile("<prompt>([a-zA-Z0-9_]+)\\s<\\s([0-9]+)\\s\\|([a-zA-Z0-9_]+)?\\|\\s([0-9]+)\\s<\\s</prompt>");


    private String name1;
    private int num1;
    private String name2;
    private int num2;

    public CoqTopLevelPrompt(String prompt) throws InvalidCoqtopResponse {
        Matcher m = p_prompt.matcher(prompt);
        if (m.matches()) {
            name1 = m.group(1);
            num1 = Integer.valueOf(m.group(2));
            name2 = m.group(2);
            num2 = Integer.valueOf(m.group(4));
        }
        else throw new InvalidCoqtopResponse(prompt);

    }

    public int getGlobalCounter(){
        return num1;
    }

    public int getProofCounter(){
        return num2;
    }

}
