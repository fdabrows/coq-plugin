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

package org.univorleans.coq.coqtop;

import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.coqtop.errors.InvalidCoqtopResponse;

import java.util.List;

public class Response {

    /**
     *
     */
    public final int globalCounter;

    /**
     *
     */
    public final int proofCounter;

    /**
     *
     */
    public final String message;

    /**
     *
     */
    public final String info;

    /**
     *
     * @param preprompt
     * @param globalCounter
     * @param proofCounter
     * @param messages
     * @throws InvalidCoqtopResponse
     */
    public Response(@Nullable List<String> preprompt, int globalCounter, int proofCounter, @Nullable List<String> messages) throws InvalidCoqtopResponse {

        this.globalCounter = globalCounter;
        this.proofCounter = proofCounter;

        String str;

        str = "";
        if (preprompt != null)
            for (int i =0; i < preprompt.size(); i++)
                str += (preprompt.get(i) + "\n");
        info = str;

        str = "";
        if (messages != null)
        for (int i =0; i < messages.size(); i++)
                str += (messages.get(i) + "\n");
        message = str;
    }
}
