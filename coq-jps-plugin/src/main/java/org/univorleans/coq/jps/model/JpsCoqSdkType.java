package org.univorleans.coq.jps.model;

import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsElement;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

import java.io.File;

/**
 * Created by dabrowski on 24/01/2016.
 */
public class JpsCoqSdkType extends JpsSdkType<JpsSimpleElement<JpsCoqSdkProperties>> {

    public static final String BYTECODE_COMPILER = "coqc";
    public static final String INTERPRETER = "coqtop";
    public static final String DEPENDENCIES = "coqdep";


    public static final JpsCoqSdkType INSTANCE = new JpsCoqSdkType();

    public String getCoqPath(@NotNull JpsElement properties) {
        return ((JpsCoqSdkProperties)((JpsSimpleElement<?>)properties).getData()).getGhcPath();
    }

    @NotNull
    public static File getByteCodeCompilerExecutable(@NotNull String sdkHome) {
        return getSdkExecutable(sdkHome, BYTECODE_COMPILER);
    }

    public static File getByteCodeInterpreterExecutable(String sdkHome) {
        return getSdkExecutable(sdkHome, INTERPRETER);
    }

    public static File getDependenciesExecutable(String sdkHome) {
        return getSdkExecutable(sdkHome, DEPENDENCIES);
    }

    @NotNull
    public static String getExecutableFileName(@NotNull String executableName) {
        return SystemInfo.isWindows ? executableName + ".exe" : executableName;
    }

    @NotNull
    private static File getSdkExecutable(@NotNull String sdkHome, @NotNull String command) {
        return new File(new File(sdkHome, "bin"), getExecutableFileName(command));
    }


}
