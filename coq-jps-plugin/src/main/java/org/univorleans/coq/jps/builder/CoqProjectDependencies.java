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

    private List<Pair<String, String>> getEdges() {
        List<Pair<String, String>> pairs = new ArrayList<>();
        Set<String> keys = dependencies.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            List<String> predecessors = dependencies.get(key);
            Iterator<String> iterator2 = predecessors.iterator();
            while (iterator2.hasNext()) {
                pairs.add(new Pair<String, String>(iterator2.next(), key));
            }
        }
        return pairs;
    }

    public Set<String> getAllFiles() {
        return dependencies.keySet();
    }

 /*   @NotNull
    public List<String> getOrderedFiles(@NotNull Collection<String> files) {
        List<String> myFiles = Arrays.asList(files.toArray(new String[0]));
        myFiles.sort((f1, f2) -> {
            if (f1.equals(f2)) return 0;
            for (Pair<String, String> p1 : getEdges()) {
                if (p1.getFirst().equals(f1) &&
                        p1.getSecond().equals(f2)) return 1;
            }
            return -1;
        });
        return myFiles;
    }*/

    @NotNull
    public List<CompileUnit> getOrderedFiles(@NotNull Collection<CompileUnit> files) {
        List<CompileUnit> myFiles = Arrays.asList(files.toArray(new CompileUnit[0]));
        myFiles.sort((f1, f2) -> {
            if (f1.file.getPath().equals(f2.file.getPath())) return 0;
            for (Pair<String, String> p1 : getEdges()) {
                if (p1.getFirst().equals(f1.file.getPath()) &&
                        p1.getSecond().equals(f2.file.getPath())) return 1;
            }
            return -1;
        });
        return myFiles;
    }


    public List<String> getDependents(String file) {

        List<String> stack = new ArrayList<>();
        List<String> result = new ArrayList<>();
        stack.add(file);
        while (!stack.isEmpty()) {
            String f = stack.remove(0);
            result.add(f);
            List <String> t = dependencies.get(f);
            if (t==null) t = new ArrayList<>();
            stack.addAll(t);
        }
        return result;
    }
}
