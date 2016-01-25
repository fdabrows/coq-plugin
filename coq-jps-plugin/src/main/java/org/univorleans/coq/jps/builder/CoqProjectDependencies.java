package org.univorleans.coq.jps.builder;

import com.intellij.openapi.util.Pair;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xmlb.annotations.AbstractCollection;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

    public CoqProjectDependencies(@NotNull List<CoqFileDependencies> files) {
        myFiles = files;
    }

    private List<Pair<String,String>> getEdges(){
        List <Pair<String,String>> pairs = new ArrayList<>();
        for (CoqFileDependencies dep : myFiles){
            for (String file :  dep.myDependencies){
                pairs.add(new Pair<>(file, dep.myPath));
            }
        }
        return pairs;
    }

    public List<String> getAllFiles(){
        List <String> list = new ArrayList<>();
        for (CoqFileDependencies dep : myFiles){
            list.add(dep.myPath);
        }
        return list;
    }

    @NotNull
    public List<String> getOrderedFiles(@NotNull List<String> files){
        files.sort((f1,f2) -> {
                    if (f1.equals(f2)) return 0;
                    for (Pair<String, String> p1 : getEdges()) {
                        if (p1.getFirst().equals(f1) &&
                                p1.getSecond().equals(f2)) return 1;
                    }
                    return -1;
                });
        return files;
    }
}
