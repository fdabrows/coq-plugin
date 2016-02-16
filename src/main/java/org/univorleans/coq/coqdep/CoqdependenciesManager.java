package org.univorleans.coq.coqdep;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;

/**
 * Created by dabrowski on 22/01/2016.
 */
public class CoqdependenciesManager extends AbstractProjectComponent {

    protected CoqdependenciesManager(Project project) {

        super(project);
    }

    /** At project loading, adds a compiler task to be executed before the coqdep.
     * This task computes coqdep and stores them in XML format */
    @Override
    public void initComponent() {

        CompilerManager compilerManager = CompilerManager.getInstance(myProject);
        StartupManager startupManager = StartupManager.getInstance(myProject);
        CoqdependenciesCompileTask task = new CoqdependenciesCompileTask();
        startupManager.registerPostStartupActivity(() -> compilerManager.addBeforeTask(task));
    }

}
