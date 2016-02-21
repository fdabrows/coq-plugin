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

package org.univorleans.coq.coqdep;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.univorleans.coq.jps.builder.CoqProjectDependencies;
import org.univorleans.coq.roots.libraries.LibraryProvider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dabrowski on 25/01/2016.
 */
public class Util {

    public static CoqProjectDependencies extractDependencies(Project project , @NotNull List<String> coqdepOutput){

        ConcurrentMap<String, List<String>> map = new ConcurrentHashMap<>();

        for (String str2 : coqdepOutput) {
            String[] parts = str2.split(":");
            String[] left = parts[0].split(" ");
            String[] right = parts[1].split(" ");
            Pattern pattern = Pattern.compile("[^.]*.vo");

            for (String a : left){
                Matcher matcher1 = pattern.matcher(a);

                if (matcher1.matches()) {
                    String path1 = a.substring(0, a.length() - 1);
                    if (!(map.containsKey(path1)))
                        map.put(path1, new ArrayList<>());
                }
            }

            for (String a : right) {
                Matcher matcher1 = pattern.matcher(a);

                if (matcher1.matches()) {

                    String path1 = a.substring(0, a.length() - 1);
                    if (!(map.containsKey(path1)))
                        map.put(path1, new ArrayList<>());
                    for (String b : left) {
                        Matcher matcher2 = pattern.matcher(b);
                        if (matcher2.matches()) {
                            String path2 = b.substring(0, b.length() - 1);
                            map.get(path1).add(path2);
                        }
                    }
                }
            }
        }

        List<Pair<String,String>> list = new ArrayList<>();
        List<Library> libraries = LibraryProvider.getLibraries(project);
        for (Library lib : libraries) {
            for (VirtualFile file : lib.getFiles(OrderRootType.CLASSES)) {
                list.add(Pair.create(file.getPath(), lib.getName()));
            }
        }

        return new CoqProjectDependencies(map);
    }
}
