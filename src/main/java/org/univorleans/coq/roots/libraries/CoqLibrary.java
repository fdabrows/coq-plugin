package org.univorleans.coq.roots.libraries;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.*;
import com.intellij.openapi.roots.libraries.ui.LibraryEditorComponent;
import com.intellij.openapi.roots.libraries.ui.LibraryPropertiesEditor;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.LibraryEditor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.util.CoqIcons;

import javax.swing.*;
import java.util.List;

/**
 * Created by dabrowski on 20/02/2016.
 */
public class CoqLibrary extends LibraryType<CoqLibraryProperties> {

    protected CoqLibrary() {
        super(new PersistentLibraryKind<CoqLibraryProperties>("coq") {
            @NotNull
            @Override
            public CoqLibraryProperties createDefaultProperties() {

                return new CoqLibraryProperties();
            }
        }
        );
    }

    @Nullable
    @Override
    public String getCreateActionName() {
        return "Coq library";
    }

    @Nullable
    @Override
    public NewLibraryConfiguration createNewLibrary(@NotNull JComponent parentComponent, @Nullable VirtualFile contextDirectory, @NotNull Project project) {

        //VirtualFile initial = findFile(System.getenv(myEnvVariable));
        //if (initial == null && GROOVY_FRAMEWORK_NAME.equals(myFrameworkName) && SystemInfo.isLinux) {
        //    initial = findFile("/usr/share/groovy");
        //}



        final FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false) {
            @Override
            public boolean isFileSelectable(VirtualFile file) {
                if (!super.isFileSelectable(file)) {
                    return false;
                }
                return true;
//                return findManager(file) != null;
            }
        };
        descriptor.setTitle("Coq " + " SDK");
        descriptor.setDescription("Choose a directory containing " + "Coq" + " library");
        final VirtualFile dir = FileChooser.chooseFile(descriptor, parentComponent, null, null);
        if (dir == null) return null;

        final CoqLibraryPresentationProvider provider = findManager(dir);
        if (provider == null) {
            return null;
        }

        final String path = dir.getPath();
        final String sdkVersion = provider.getSDKVersion(path);
       /* if (AbstractConfigUtils.UNDEFINED_VERSION.equals(sdkVersion)) {
            Messages.showErrorDialog(parentComponent,
                    "Looks like " + "Coq" + " distribution in specified path is broken. Cannot determine version.",
                    "Failed to Create Library");
            return null;
        }*/
        return new NewLibraryConfiguration(provider.getLibraryPrefix() + "-" + sdkVersion) {
            @Override
            public void addRoots(@NotNull LibraryEditor editor) {
                provider.fillLibrary(path, editor);
            }
        };
    }

    @Nullable
    //@Override
    public LibraryPropertiesEditor createPropertiesEditor(@NotNull LibraryEditorComponent<CoqLibraryProperties> editorComponent) {
        return null;
    }


    @Nullable
    public static CoqLibraryPresentationProvider findManager(@NotNull VirtualFile dir) {
        final String name = dir.getName();

        final List<CoqLibraryPresentationProvider> providers = ContainerUtil.findAll(LibraryPresentationProvider.EP_NAME.getExtensions(), CoqLibraryPresentationProvider.class);
        for (final CoqLibraryPresentationProvider provider : providers) {
            if (provider.managesName(name) && provider.isSDKHome(dir)) {
                return provider;
            }
        }

        for (final CoqLibraryPresentationProvider manager : providers) {
            if (manager.isSDKHome(dir)) {
                return manager;
            }
        }
        return null;
    }



    public Icon getIcon() {
        return CoqIcons.FILE;
    }

}
