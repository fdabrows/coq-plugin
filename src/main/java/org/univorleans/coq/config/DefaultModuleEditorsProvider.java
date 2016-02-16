package org.univorleans.coq.config;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ui.configuration.ClasspathEditor;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.openapi.roots.ui.configuration.OutputEditor;
import org.univorleans.coq.modules.CoqModuleType;

import javax.swing.*;
import java.awt.*;

/**
 * Created by dabrowski on 14/02/2016.
 */
public class DefaultModuleEditorsProvider implements ModuleConfigurationEditorProvider {
    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState state) {
        Module module = state.getRootModel().getModule();
        if (ModuleType.get(module) instanceof CoqModuleType) {
            return new ModuleConfigurationEditor[]{
                    new CoqContentEntriesEditor(module.getName(), state),
                    //new OutputEditorEx(state),
                    new ClasspathEditor(state)
            };
        }
        return ModuleConfigurationEditor.EMPTY;
    }

    static class OutputEditorEx extends OutputEditor {
        protected OutputEditorEx(ModuleConfigurationState state) {
            super(state);
        }

        protected JComponent createComponentImpl() {
            JComponent component = super.createComponentImpl();
            for (Component comp :  component.getComponents())
                System.out.println(comp.getClass());
            component.remove(1);
            component.remove(1);
            //component.remove(1); // todo: looks ugly
            return component;
        }
    }
}
