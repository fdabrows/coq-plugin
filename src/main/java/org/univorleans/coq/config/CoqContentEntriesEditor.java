package org.univorleans.coq.config;

import com.intellij.openapi.roots.ui.configuration.CommonContentEntriesEditor;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.jetbrains.jps.model.java.JavaSourceRootType;

/**
 * Created by dabrowski on 14/02/2016.
 */
public class CoqContentEntriesEditor extends CommonContentEntriesEditor {
    public CoqContentEntriesEditor(String moduleName, ModuleConfigurationState state) {
        super(moduleName, state, JavaSourceRootType.SOURCE, JavaSourceRootType.TEST_SOURCE, CoqIncludeSourceRootType.INSTANCE);
    }
}
