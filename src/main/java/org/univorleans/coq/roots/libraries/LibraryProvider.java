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

package org.univorleans.coq.roots.libraries;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dabrowski on 20/02/2016.
 */
public class LibraryProvider {

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
