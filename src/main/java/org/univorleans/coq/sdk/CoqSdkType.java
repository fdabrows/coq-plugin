package org.univorleans.coq.sdk;

import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.util.SystemInfo;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.jps.model.JpsCoqModelSerializerExtension;
import org.univorleans.coq.jps.model.JpsCoqSdkType;

import java.io.File;

/**
 * Created by dabrowski on 20/01/2016.
 */
public class CoqSdkType extends SdkType{

    public static final CoqSdkType INSTANCE = new CoqSdkType();

    public CoqSdkType() {

        super(JpsCoqModelSerializerExtension.COQ_SDK_TYPE_ID);
    }

    @NotNull
    public static CoqSdkType getInstance() {

        CoqSdkType instance = SdkType.findInstance(CoqSdkType.class);
        assert instance != null : "Make sure JpsCoqSdkType is registered in plugin.xml";
        return instance;
    }

    @Nullable
    @Override
    public String suggestHomePath() {
        String path = null;
        if (SystemInfo.isWindows) {
            path =  "C:\\Program Files (x86)\\coq\\bin";
        }
        else if (SystemInfo.isMac) {
            path = "/usr/local";
        }
        else if (SystemInfo.isLinux) {
            path = "/usr/lib";
        }
        return path;
    }

    @Override
    public String suggestSdkName(String s, String s1) {
        return "Coq SDK";
    }

    @Override
    public boolean isValidSdkHome(String path) {
        File coqtop = JpsCoqSdkType.getByteCodeInterpreterExecutable(path);
        File coqc = JpsCoqSdkType.getByteCodeCompilerExecutable(path);
        return coqtop.canExecute() && coqtop.canExecute();
    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        return null;
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData sdkAdditionalData, @NotNull Element element) {

    }


    @Override
    public String getPresentableName() {

        return "Coq SDK";
    }

    public File getBinDirectory(String path) {
        return new File(path, "bin");

    }

    @Nullable
    @Override
    public String getVersionString(@NotNull String sdkHome) {

        String cmd = getBinDirectory(sdkHome).getAbsolutePath() + File.separator + JpsCoqSdkType.BYTECODE_COMPILER;
        return "Unknown";
    }

}
