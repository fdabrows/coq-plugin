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
        assert instance != null : "Make sure CoqSdkType is registered in plugin.xml";
        return instance;
    }

    @Nullable
    @Override
    public String suggestHomePath() {
        if (SystemInfo.isWindows) {
            return "C:\\Program Files (x86)\\coq\\bin";
        }
        else if (SystemInfo.isMac) {
            String macPorts = "/usr/local";
            return macPorts;
        }
        else if (SystemInfo.isLinux) {
            return "/usr/lib";
        }
        return null;
    }

    @Override
    public boolean isValidSdkHome(String path) {
        File erl = JpsCoqSdkType.getByteCodeInterpreterExecutable(path);
        File erlc = JpsCoqSdkType.getByteCodeCompilerExecutable(path);
        return erl.canExecute() && erlc.canExecute();
    }

    @Override
    public String suggestSdkName(String s, String s1) {
        return "Coq SDK";
    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        return null;
    }

    @Override
    public String getPresentableName() {
        return "Coq SDK";
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData sdkAdditionalData, @NotNull Element element) {

    }

    @Nullable
    @Override
    public String getVersionString(@NotNull String sdkHome) {
        return "Unknown";
    }

}
