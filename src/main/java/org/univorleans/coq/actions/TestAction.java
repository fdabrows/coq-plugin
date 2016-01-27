package org.univorleans.coq.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.jps.incremental.BuilderService;
import org.univorleans.coq.jps.builder.CoqBuilderService;
import org.univorleans.coq.toplevel.CoqtopEngine;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by dabrowski on 22/01/2016.
 */
public class TestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project p = anActionEvent.getProject();
        Editor editor = FileEditorManager.getInstance(p).getSelectedTextEditor();
        CoqtopEngine.getEngine(editor);

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

// CoqdependenciesCompileTask
// CoqModuleBuildOrderBuilder