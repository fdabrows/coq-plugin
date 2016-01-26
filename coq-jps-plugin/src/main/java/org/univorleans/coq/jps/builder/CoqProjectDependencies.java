package org.univorleans.coq.jps.builder;

import com.intellij.openapi.util.Pair;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xmlb.annotations.AbstractCollection;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by dabrowski on 22/01/2016.
 */
public class CoqProjectDependencies {
    @Tag("files")
    @AbstractCollection(surroundWithTag = false, elementTag = "file")
    public Map<String, List<String>> dependencies;

    @SuppressWarnings("unused") // reflection
    public CoqProjectDependencies() {
    }

    public CoqProjectDependencies(@NotNull Map<String, List<String>> dependencies) {
        this.dependencies = dependencies;
    }

    private List<Pair<String,String>> getEdges(){
        List <Pair<String,String>> pairs = new ArrayList<>();
        Set<String> keys = dependencies.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            List<String> predecessors = dependencies.get(key);
            Iterator<String> iterator2 = predecessors.iterator();
            while (iterator2.hasNext()){
                pairs.add(new Pair<String,String>(iterator2.next(), key));
            }
        }
        return pairs;
    }

    public Set<String> getAllFiles(){
        return dependencies.keySet();
    }

    @NotNull
    public List<String> getOrderedFiles(@NotNull Collection<String> files){
        List <String> myFiles = Arrays.asList(files.toArray(new String[0]));
        myFiles.sort((f1,f2) -> {
                    if (f1.equals(f2)) return 0;
                    for (Pair<String, String> p1 : getEdges()) {
                        if (p1.getFirst().equals(f1) &&
                                p1.getSecond().equals(f2)) return 1;
                    }
                    return -1;
                });
        return myFiles;
    }
}
