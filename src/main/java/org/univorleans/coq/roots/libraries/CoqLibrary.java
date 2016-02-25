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

package org.univorleans.coq.roots.libraries;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.*;
import com.intellij.openapi.roots.libraries.ui.LibraryEditorComponent;
import com.intellij.openapi.roots.libraries.ui.LibraryPropertiesEditor;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.LibraryEditor;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.util.CoqIcons;

import javax.swing.*;
import java.io.File;

/**
 * Created by dabrowski on 20/02/2016.
 */
public class CoqLibrary extends LibraryType<DummyLibraryProperties> {

    protected CoqLibrary() {
        super(new PersistentLibraryKind<DummyLibraryProperties>("coq") {
            @NotNull
            @Override
            public DummyLibraryProperties createDefaultProperties() {

                return new DummyLibraryProperties();
            }
        }
        );
    }

    @Nullable
    @Override
    /**
     * @return : text to show in 'New Library' popup
     */
    public String getCreateActionName() {
        return "Coq library";
    }


    /**
     * Called when a new library of this type is created in Project Structure dialog
     * @param parentComponent
     * @param contextDirectory
     * @param project
     * @return
     */
    @Nullable
    @Override
    public NewLibraryConfiguration createNewLibrary(@NotNull JComponent parentComponent, @Nullable VirtualFile contextDirectory, @NotNull Project project) {

        final FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        descriptor.setTitle("Select Library Files");
        descriptor.setDescription("Select files or directories in which libraries are located");

        final VirtualFile dir = FileChooser.chooseFile(descriptor, parentComponent, null, null);

        if (dir == null) return null;

        return new NewLibraryConfiguration("Unnamed") {
            @Override
            public void addRoots(@NotNull LibraryEditor editor) {

                File srcRoot = new File(dir.getPath());
                if (srcRoot.exists() && srcRoot.isDirectory()){
                    String url = VfsUtil.getUrlForLibraryRoot(srcRoot);
                    editor.addRoot(url, OrderRootType.CLASSES);
                }
            }
        };
    }


    @Nullable
    @Override
    public LibraryPropertiesEditor createPropertiesEditor(@NotNull LibraryEditorComponent<DummyLibraryProperties> editorComponent) {
        return null;
    }

    @NotNull
    @Override
    public Icon getIcon(LibraryProperties properties) {
        return CoqIcons.FILE;
    }
}
