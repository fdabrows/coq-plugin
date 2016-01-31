package org.univorleans.coq.modules;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

/**
 * Created by dabrowski on 20/01/2016.
 */
public class CoqModuleBuilder extends JavaModuleBuilder {

    private final String[] sourceFolders  = new String[] {"src"};
    private String[] excludeFolders = new String[] {"doc", "ext"};


    @Override
    public void setupRootModel(ModifiableRootModel modifiableRootModel) throws ConfigurationException {

      /*  ContentEntry contentEntry = doAddContentEntry(modifiableRootModel);
        if (contentEntry != null) {
            String rootPath = getContentEntryPath();
            for (String name : sourceFolders){
                String folder = rootPath + File.separator + name;
                File file = new File(folder);
                file.mkdir();
                VirtualFile dir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
                if (dir != null) contentEntry.addSourceFolder(dir, false);
            }
            for (String name : excludeFolders){
                String folder = rootPath + File.separator + name;
                File file = new File(folder);
                file.mkdir();
                VirtualFile dir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
                if (dir != null) contentEntry.addExcludeFolder(dir);
            }
        }*/
        ContentEntry contentEntry = doAddContentEntry(modifiableRootModel);
        String rootPath = getContentEntryPath();
        String folder = rootPath + File.separator + "out";
        File file = new File(folder);
        file.mkdir();
        VirtualFile dir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
        if (dir != null) contentEntry.addExcludeFolder(dir);

        super.setupRootModel(modifiableRootModel);
        modifiableRootModel.inheritSdk();
    }

    @Override
    public ModuleType getModuleType() {
        return CoqModuleType.getInstance();
    }


}
