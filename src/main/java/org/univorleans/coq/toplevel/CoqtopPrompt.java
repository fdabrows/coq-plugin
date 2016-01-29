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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoqtopPrompt {

    // TODO : modify pattern for nested lemmas
    private static final Pattern p_prompt =
            Pattern.compile("<prompt>([a-zA-Z0-9_]+)\\s<\\s([0-9]+)\\s\\|(([a-zA-Z0-9_]+\\|)+|\\|)\\s([0-9]+)\\s<\\s</prompt>");


    private final String currentLemma;
    private final int globalCounter;
    private final String openedLemmas;
    private final int proofCounter;

    private CoqtopPrompt(String prompt) throws InvalidCoqtopResponse {
        Matcher m = p_prompt.matcher(prompt);
        if (m.matches()) {
            currentLemma = m.group(1);
            globalCounter = Integer.valueOf(m.group(2));
            openedLemmas = m.group(4);
            proofCounter = Integer.valueOf(m.group(5));
        }
        else throw new InvalidCoqtopResponse(prompt);
    }

    public static CoqtopPrompt makePrompt(String prompt) throws InvalidCoqtopResponse {
        return new CoqtopPrompt(prompt);
    }

    public int getGlobalCounter(){
        return globalCounter;
    }

    public int getProofCounter(){
        return proofCounter;
    }

    public String getCurrentLemma(){ return currentLemma; }

    public List<String> getOpenedLemmas(){ return new ArrayList<>();}

}
