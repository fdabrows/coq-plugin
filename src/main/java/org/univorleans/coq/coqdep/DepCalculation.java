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

package org.univorleans.coq.coqdep;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;

/**
 * Created by dabrowski on 22/01/2016.
 */
public class DepCalculation extends AbstractProjectComponent {

    protected DepCalculation(Project project) {

        super(project);
    }

    /**
     * At project loading, adds a compiler task to be executed before the coqdep.
     * This task computes dependencies and stores them in XML format
     */
    @Override
    public void initComponent() {

        CompilerManager compilerManager = CompilerManager.getInstance(myProject);
        StartupManager startupManager = StartupManager.getInstance(myProject);
        DepCalculationTask task = new DepCalculationTask();
        startupManager.registerPostStartupActivity(() -> compilerManager.addBeforeTask(task));
    }

}
