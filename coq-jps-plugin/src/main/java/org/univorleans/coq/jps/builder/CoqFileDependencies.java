package org.univorleans.coq.jps.builder;

import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xmlb.annotations.AbstractCollection;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by dabrowski on 22/01/2016.
 */
public class CoqFileDependencies {

    @Attribute("path")
    private String myPath;

    @Tag("dependencies")
    @AbstractCollection(surroundWithTag = false, elementTag = "dependency")
    public List<String> myDependencies = ContainerUtil.newArrayList();

    @SuppressWarnings("unused") // reflection
    public CoqFileDependencies() {
    }

    public CoqFileDependencies(@NotNull String path, @NotNull List<String> dependencies) {
        myPath = path;
        myDependencies = dependencies;
    }
}
