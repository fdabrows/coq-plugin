package org.univorleans.coq.jps.model;

import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

import java.io.File;

/**
 * Created by dabrowski on 20/01/2016.
 */
public class JpsCoqSdkType extends JpsSdkType<JpsDummyElement> implements JpsElementTypeWithDefaultProperties<JpsDummyElement> {

    public static final JpsCoqSdkType INSTANCE = new JpsCoqSdkType();

    public static final String BYTECODE_INTERPRETER = "coqtop";
    public static final String BYTECODE_COMPILER = "coqc";
    public static final String SCRIPT_INTERPRETER = "coqtop";

    private static final String TESTS_SDK_PATH_PROPERTY = "erlang.sdk.path";
    private static final String DEFAULT_TESTS_SDK_PATH = "/usr/lib/coq/";


    @NotNull
    public static File getByteCodeInterpreterExecutable(@NotNull String sdkHome) {
        return getSdkExecutable(sdkHome, BYTECODE_INTERPRETER);
    }

    @NotNull
    public static File getByteCodeCompilerExecutable(@NotNull String sdkHome) {
        return getSdkExecutable(sdkHome, BYTECODE_COMPILER);
    }

    @NotNull
    public static File getScriptInterpreterExecutable(@NotNull String sdkHome) {
        return getSdkExecutable(sdkHome, SCRIPT_INTERPRETER);
    }

    @NotNull
    @Override
    public JpsDummyElement createDefaultProperties() {
        return null;
    }

    @NotNull
    public static String getExecutableFileName(@NotNull String executableName) {
        return SystemInfo.isWindows ? executableName + ".exe" : executableName;
    }

    @TestOnly
    @NotNull
    public static String getTestsSdkPath() {
        String sdkPathFromProperty = StringUtil.nullize(System.getProperty(TESTS_SDK_PATH_PROPERTY));
        return StringUtil.notNullize(sdkPathFromProperty, DEFAULT_TESTS_SDK_PATH);
    }

    @TestOnly
    @NotNull
    public static String getSdkConfigurationFailureMessage() {
        return "Failed to setup an Erlang SDK at " + getTestsSdkPath() +
                "\nUse " + TESTS_SDK_PATH_PROPERTY + " system property to set sdk path";
    }

    @NotNull
    private static File getSdkExecutable(@NotNull String sdkHome, @NotNull String command) {
        return new File(new File(sdkHome, "bin"), getExecutableFileName(command));
    }
}
