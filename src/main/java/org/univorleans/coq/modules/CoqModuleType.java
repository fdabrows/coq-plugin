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

package org.univorleans.coq.modules;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.annotations.NotNull;
import org.univorleans.coq.jps.model.JpsCoqSdkType;
import org.univorleans.coq.util.CoqIcons;

import javax.swing.*;

/**
 * Created by dabrowski on 20/01/2016.
 */
public class CoqModuleType extends ModuleType<CoqModuleBuilder> {

    public static final String MODULE_TYPE_ID = "COQ_MODULE";

    public CoqModuleType(){
        super(MODULE_TYPE_ID);
    }

    public static CoqModuleType getInstance() {
        return (CoqModuleType) ModuleTypeManager.getInstance().findByID(MODULE_TYPE_ID);
    }

    @NotNull
    @Override
    public CoqModuleBuilder createModuleBuilder() {
        return new CoqModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "Coq";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Create a new Coq Project";
    }

    @Override
    public Icon getBigIcon() { return CoqIcons.FILE; }

    @Override
    public Icon getNodeIcon(@Deprecated boolean b) {
        return CoqIcons.FILE;
    }


    @Override
    public boolean isValidSdk(Module module, Sdk projectSdk) {
        return projectSdk.getSdkType() == JpsCoqSdkType.INSTANCE;
    }

}
