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

import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.Pair;
import com.intellij.util.SystemProperties;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xmlb.SkipDefaultValuesSerializationFilters;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.annotations.AbstractCollection;
import com.intellij.util.xmlb.annotations.Tag;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.incremental.CompileContext;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dabrowski on 22/01/2016.
 */
public class CoqProjectDependencies {
    @Tag("files")
    @AbstractCollection(surroundWithTag = false, elementTag = "file")

    public Map<String, List<String>> dependencies;

    //public List<Pair<String, String>> libraries;

    @SuppressWarnings("unused") // reflection
    public CoqProjectDependencies() {

    }

    public CoqProjectDependencies(@NotNull Map<String, List<String>> dependencies) {

        this.dependencies = dependencies;
    }


    public static CoqProjectDependencies buildDependencies(@NotNull List<String> coqdepOutput){
        ConcurrentMap<String, List<String>> map = new ConcurrentHashMap<>();

        for (String str2 : coqdepOutput) {
            String[] parts = str2.split(":");
            String[] left = parts[0].split(" ");
            String[] right = parts[1].split(" ");
            Pattern pattern = Pattern.compile("[^.]*.vo");

            for (String a : left) {
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
        return new CoqProjectDependencies(map);
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

    public static void write(File file, CoqProjectDependencies dependencies) throws IOException {


        org.jdom.Document serializedDocument =
                new org.jdom.Document(XmlSerializer.serialize(dependencies,
                        new SkipDefaultValuesSerializationFilters()));
        JDOMUtil.writeDocument(serializedDocument, file, SystemProperties.getLineSeparator());
    }

    public static CoqProjectDependencies read(File datastorageRoot){
        return readFromXML(datastorageRoot,
                CoqBuilderUtil.BUILD_ORDER_FILE_NAME,
                CoqProjectDependencies.class);
    }

    @Nullable
    public static <T> T readFromXML(@NotNull File datastorageRoot, @NotNull String filename, @NotNull Class<T> tClass) {
        try {
            File xmlFile = getXmlFile(datastorageRoot, filename);
            if (!xmlFile.exists()) return null;

            Document document = JDOMUtil.loadDocument(xmlFile);
            return XmlSerializer.deserialize(document, tClass);
        }
        catch (JDOMException e) {
            //LOG.error("Can't read XML from " + filename, e);
        }
        catch (IOException e) {
            //LOG.warn("Can't read " + filename, e);
        }
        return null;
    }

    @NotNull
    public static File getXmlFile(File dataStorageRoot, @NotNull String filename) {

        //File dataStorageRoot = context.getProjectDescriptor().dataManager.getDataPaths().getDataStorageRoot();
        File parentDirectory = new File(dataStorageRoot, CoqBuilderUtil.BUILDER_DIRECTORY);
        return new File(parentDirectory, filename);
    }
}
