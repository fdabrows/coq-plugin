package org.univorleans.coq.files;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by dabrowski on 20/01/2016.
 */
public class CoqFileTypeFactory extends FileTypeFactory {

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(CoqFileType.INSTANCE, CoqFileType.DEFAULT_EXTENSION);
    }
}
