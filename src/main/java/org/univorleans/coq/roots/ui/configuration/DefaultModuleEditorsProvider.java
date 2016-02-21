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

package org.univorleans.coq.roots.ui.configuration;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ui.configuration.ClasspathEditor;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.univorleans.coq.modules.CoqModuleType;


/**
 * Module Window of project structure
 * CoqContentEntriesEditor : directories, src, excluded
 * ClasspathEditor : SDK, libraries
 */

public class DefaultModuleEditorsProvider implements ModuleConfigurationEditorProvider {

    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState state) {

        Module module = state.getRootModel().getModule();

        if (ModuleType.get(module) instanceof CoqModuleType) {
            return new ModuleConfigurationEditor[]{
                    new CoqContentEntriesEditor(module.getName(), state),
                    new ClasspathEditor(state)
            };
        }
        return ModuleConfigurationEditor.EMPTY;
    }

}
