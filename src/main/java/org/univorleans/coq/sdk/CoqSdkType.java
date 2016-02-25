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

package org.univorleans.coq.sdk;

import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.util.SystemInfo;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.jps.model.JpsCoqModelSerializerExtension;
import org.univorleans.coq.jps.model.JpsCoqSdkType;

import java.io.File;

/**
 * Created by dabrowski on 20/01/2016.
 */
public class CoqSdkType extends SdkType{

    public static final CoqSdkType INSTANCE = new CoqSdkType();

    public CoqSdkType() {

        super(JpsCoqModelSerializerExtension.COQ_SDK_TYPE_ID);
    }

    @NotNull
    public static CoqSdkType getInstance() {

        CoqSdkType instance = SdkType.findInstance(CoqSdkType.class);
        assert instance != null : "Make sure JpsCoqSdkType is registered in plugin.xml";
        return instance;
    }

    @Nullable
    @Override
    public String suggestHomePath() {
        String path = null;
        if (SystemInfo.isWindows) {
            path =  "C:\\Program Files (x86)\\coq\\bin";
        }
        else if (SystemInfo.isMac) {
            path = "/usr/local";
        }
        else if (SystemInfo.isLinux) {
            path = "/usr/lib";
        }
        return path;
    }

    @Override
    public String suggestSdkName(String s, String s1) {
        return "Coq SDK";
    }

    @Override
    public boolean isValidSdkHome(String path) {
        File coqtop = JpsCoqSdkType.getByteCodeInterpreterExecutable(path);
        File coqc = JpsCoqSdkType.getByteCodeCompilerExecutable(path);
        return coqtop.canExecute() && coqc.canExecute();
    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        return null;
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData sdkAdditionalData, @NotNull Element element) {

    }

    @Override
    public String getPresentableName() {

        return "Coq SDK";
    }

    @Nullable
    @Override
    public String getVersionString(@NotNull String sdkHome) {

        return "Unknown";
    }

}
