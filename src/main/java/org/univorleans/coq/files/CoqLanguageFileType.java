package org.univorleans.coq.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.CoqIcons;

import javax.swing.*;

/**
 * Created by dabrowski on 20/01/2016.
 */
public class CoqLanguageFileType extends LanguageFileType{

    public static CoqLanguageFileType INSTANCE = new CoqLanguageFileType();

    private CoqLanguageFileType(){
        super(CoqLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Coq File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Coq";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "v";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return CoqIcons.FILE;
    }

    @Override
    public String getCharset(@NotNull VirtualFile file, byte[] content) {
        return "UTF8";
    }
}
