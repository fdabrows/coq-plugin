package org.univorleans.coq.util;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.roots.libraries.LibraryKind;
import com.intellij.openapi.roots.libraries.LibraryPresentationProvider;
import com.intellij.openapi.roots.libraries.LibraryProperties;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.util.CoqIcons;

import javax.swing.*;
import java.util.List;

/**
 * Created by dabrowski on 13/02/2016.
 */
public class CoqLibraryPresentationProvider<P extends LibraryProperties> extends LibraryPresentationProvider<P> {


    protected CoqLibraryPresentationProvider(@NotNull LibraryKind kind) {
        super(kind);
    }

    @Nullable
    @Override
    public P detect(@NotNull List<VirtualFile> list) {
        return null;
    }

    @Override
    public Icon getIcon(){
        return CoqIcons.FILE;

    }

    @Nullable
    public String getDescription(@NotNull P properties) {
        return "Coq Library";
    }

}
