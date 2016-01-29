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

package org.univorleans.coq.util;

import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * Created by dabrowski on 27/01/2016.
 */
public class ProcessChannels {

    public final BufferedReader output;
    public final BufferedWriter input;
    public final BufferedReader error;

    public ProcessChannels(@NotNull Process process){
        output = new BufferedReader(new InputStreamReader(process.getInputStream()));
        input = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    }

    public void close() throws IOException {
        output.close();
        input.close();
        error.close();
    }


}
