package org.univorleans.coq.jps.builder;

import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xmlb.annotations.AbstractCollection;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by dabrowski on 22/01/2016.
 */
public class CoqProjectDependencies {
    @Tag("files")
    @AbstractCollection(surroundWithTag = false, elementTag = "file")
    public List<CoqFileDependencies> myFiles = ContainerUtil.newArrayList();

    @SuppressWarnings("unused") // reflection
    public CoqProjectDependencies() {
    }

    public CoqProjectDependencies(@NotNull List<CoqFileDependencies> topologicallySortedErlangFilesDescriptors) {
        myFiles = topologicallySortedErlangFilesDescriptors;
    }
}
