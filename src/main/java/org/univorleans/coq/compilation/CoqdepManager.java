package org.univorleans.coq.compilation;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;

/**
 * Created by dabrowski on 22/01/2016.
 */
public class CoqdepManager extends AbstractProjectComponent {

    protected CoqdepManager(Project project) {

        super(project);
    }

    /* Adds a compiler task to be executed before the compilation */
    /* StartupManager : to add a task which is run during project loading */
    @Override
    public void initComponent() {

        CompilerManager compilerManager = CompilerManager.getInstance(myProject);
        StartupManager startupManager = StartupManager.getInstance(myProject);
        CoqdepCompileTask task = new CoqdepCompileTask();
        startupManager.registerPostStartupActivity(() -> compilerManager.addBeforeTask(task));
    }

}
