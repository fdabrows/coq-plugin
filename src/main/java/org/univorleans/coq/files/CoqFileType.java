package org.univorleans.coq.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.util.CoqIcons;

import javax.swing.*;

/**
 * Created by dabrowski on 20/01/2016.
 */
public class CoqFileType extends LanguageFileType{

    public final static CoqFileType INSTANCE = new CoqFileType();

    public final static String NAME = "COQSOURCE";
    public final static String DEFAULT_EXTENSION = "v";

    private CoqFileType(){
        super(CoqLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return NAME;
    }

    @NotNull
    @Override
    public String getDescription() { return "Coq source file"; }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return CoqIcons.FILE;
    }

    @Override
    public String getCharset(@NotNull VirtualFile file, byte[] content) {
        return "UTF-8";
    }
}
