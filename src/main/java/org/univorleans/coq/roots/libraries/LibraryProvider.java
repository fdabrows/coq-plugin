package org.univorleans.coq.roots.libraries;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dabrowski on 20/02/2016.
 */
public class LibraryProvider {

    private static OrderRootType rootType = OrderRootType.CLASSES;

    public static List<Library> getGlobalLibraries() {

        List<Library> list = new ArrayList<>();

        Library[] libraries =
                LibraryTablesRegistrar.getInstance().getLibraryTable().getLibraries();

        for (Library lib : libraries) {
                list.add(lib);
        }
        return list;
    }

    public static List<Library> getLocalLibraries(Project project) {

        List<Library> list = new ArrayList<>();

        Library[] libraries =
                LibraryTablesRegistrar.getInstance().getLibraryTable(project).getLibraries();

        for (Library lib : libraries) {
                list.add(lib);
        }
        return list;
    }

    public static List<Library> getLibraries(Project project) {


        List<Library> files = getLocalLibraries(project);
        files.addAll(getGlobalLibraries());
        return files;
    }
}
