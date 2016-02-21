package org.univorleans.coq.roots.libraries;

import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.LibraryKind;
import com.intellij.openapi.roots.libraries.LibraryPresentationProvider;
import com.intellij.openapi.roots.libraries.LibraryProperties;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.LibraryEditor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * Created by dabrowski on 20/02/2016.
 */
public class CoqLibraryPresentationProvider extends LibraryPresentationProvider<CoqLibraryProperties> {

    public static final LibraryKind COQ_KIND = LibraryKind.create("coq  ");

    protected CoqLibraryPresentationProvider() {

        super(COQ_KIND);
    }

    @Override
    public String getDescription(@NotNull CoqLibraryProperties properties) {
        final String version = properties.getVersionString();
        return getLibraryCategoryName() + " library" + (version != null ? " of version " + version : ":");
    }

    @NotNull
    public String getSDKVersion(String path) {
        return "coqsdk";
    }

    @NotNull
    @Nls
    public String getLibraryPrefix() {
        return StringUtil.toLowerCase(getLibraryCategoryName());
    }

    @Nls
    @NotNull
    public String getLibraryCategoryName() {
        return "Coq";
    }

    public boolean managesName(@NotNull String name) {
        return StringUtil.startsWithIgnoreCase(name, getLibraryPrefix());
    }
    public boolean isSDKHome(@NotNull VirtualFile file) {
        return true;
//        return GroovyConfigUtils.getInstance().isSDKHome(file);
    }

    @Override
    public CoqLibraryProperties detect(@NotNull List<VirtualFile> classesRoots) {
        final VirtualFile[] libraryFiles = VfsUtilCore.toVirtualFileArray(classesRoots);
       // if (managesLibrary(libraryFiles)) {
       //     final String version = getLibraryVersion(libraryFiles);
       //     return new GroovyLibraryProperties(version);
       // }
        return null;
    }

    protected void fillLibrary(String path, LibraryEditor libraryEditor) {

        File srcRoot = new File(path);
        if (srcRoot.exists() && srcRoot.isDirectory()){
            String vfile = VfsUtil.getUrlForLibraryRoot(srcRoot);
            libraryEditor.addRoot(vfile, OrderRootType.CLASSES);
        }
/*
        File srcRoot = new File(path + "/src/main");
        if (srcRoot.exists()) {
            libraryEditor.addRoot(VfsUtil.getUrlForLibraryRoot(srcRoot), OrderRootType.SOURCES);
        }

        File[] jars;
        File libDir = new File(path + "/lib");
        if (libDir.exists()) {
            jars = libDir.listFiles();
        } else {
            jars = new File(path + "/embeddable").listFiles();
        }
        if (jars != null) {
            for (File file : jars) {
                if (file.getName().endsWith(".jar")) {
                    libraryEditor.addRoot(VfsUtil.getUrlForLibraryRoot(file), OrderRootType.CLASSES);
                }
            }
        }*/
    }


}
