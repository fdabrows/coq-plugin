package org.univorleans.coq.coqtop.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by dabrowski on 22/01/2016.
 */
public class TestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

    }
}

// services -> META-INF du build principal
// ERLANG
// - META-INF/plugin.xml
// - Classes/...
// - lib/jps-plugin.jar
// jps-plugin.jar
// - META-INF
// -- services
// -- org

// DepCalculationTask
// CoqModuleBuildOrderBuilder