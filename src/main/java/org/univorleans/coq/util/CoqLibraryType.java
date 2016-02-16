package org.univorleans.coq.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.*;
import com.intellij.openapi.roots.libraries.ui.LibraryEditorComponent;
import com.intellij.openapi.roots.libraries.ui.LibraryPropertiesEditor;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by dabrowski on 13/02/2016.
 */

public class CoqLibraryType<P extends LibraryProperties> extends LibraryType<P> {


    protected CoqLibraryType(@NotNull PersistentLibraryKind<P> libraryKind) {
        super(libraryKind);
    }

    @Nullable
    @Override
    public String getCreateActionName() {
        return "Coq Library";
    }

    @Nullable
    @Override
    public NewLibraryConfiguration createNewLibrary(@NotNull JComponent jComponent, @Nullable VirtualFile virtualFile, @NotNull Project project) {
        return null;
    }

    @Nullable
    @Override
    public LibraryPropertiesEditor createPropertiesEditor(@NotNull LibraryEditorComponent<P> libraryEditorComponent) {
        return null;
    }
}
