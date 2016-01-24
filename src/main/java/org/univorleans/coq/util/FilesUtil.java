package org.univorleans.coq.util;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dabrowski on 24/01/2016.
 */
public class FilesUtil {

    public static VirtualFile[] getSubdirs(VirtualFile file) {

        VirtualFile[] files = file.getChildren();
        List<VirtualFile> dirs = new ArrayList<>();

        for (VirtualFile f : files) {
            if (f.isDirectory()) {
                dirs.add(f);
                dirs.addAll(Arrays.asList(getSubdirs(f)));
            }
        }
        return dirs.toArray(new VirtualFile[0]);
    }
}
