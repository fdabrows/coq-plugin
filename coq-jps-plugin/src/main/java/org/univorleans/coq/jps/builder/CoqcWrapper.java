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

package org.univorleans.coq.jps.builder;

import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.univorleans.coq.jps.model.JpsCoqSdkProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dabrowski on 25/01/2016.
 */
public class CoqcWrapper  {

    String[] cmd;
    JpsSdk<JpsSimpleElement<JpsCoqSdkProperties>> sdk;
    File[] includes;
    List<String> lines = new ArrayList<>();

    public CoqcWrapper(JpsSdk<JpsSimpleElement<JpsCoqSdkProperties>> projectSdk, File[] includes) {

        this.sdk = projectSdk;
        this.includes = includes;

    }

    public boolean compile(File path){

        Runtime runtime = Runtime.getRuntime();

        Process coqdep;
        try {

            List<String> cmd = new ArrayList<>();
            cmd.add(sdk.getHomePath() + "/bin/coqc");

            for (File file : includes) {cmd.add("-I"); cmd.add(file.getPath());}
            cmd.add(path.getPath());
            this.cmd = cmd.toArray(new String[0]);

            coqdep = runtime.exec(this.cmd);
            lines = new ArrayList<>();
            if (coqdep.waitFor() > 0) {
                BufferedReader processError = new BufferedReader(new InputStreamReader(coqdep.getInputStream()));
                BufferedReader processError2 = new BufferedReader(new InputStreamReader(coqdep.getInputStream()));

                String str;
                while ((str = processError.readLine()) != null){
                    lines.add(str);
                }
                while ((str = processError2.readLine()) != null){
                    lines.add(str);
                }
                return false;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ErrorMessage getErrorMessage(){
        return new ErrorMessage(lines);
    }

    public String toString(){
        String msg = "";
        for (String str : cmd){
            msg += str+ " ";
        }
        return msg;
    }

}
