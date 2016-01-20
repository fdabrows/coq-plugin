package org.univorleans.coq.modules;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.annotations.NotNull;
import org.univorleans.coq.CoqIcons;

import javax.swing.*;

/**
 * Created by dabrowski on 20/01/2016.
 */
public class CoqModuleType extends ModuleType<CoqModuleBuilder> {

    public static final CoqModuleType INSTANCE = new CoqModuleType();

    public CoqModuleType(){
        super("COQ_MODULE");
    }

    @NotNull
    @Override
    public CoqModuleBuilder createModuleBuilder() {
        return new CoqModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "Coq";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Create a new Coq Project";
    }

    @Override
    public Icon getBigIcon() {
        return CoqIcons.FILE;

    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean b) {
        return CoqIcons.FILE;
    }


    @Override
    public boolean isValidSdk(Module module, Sdk projectSdk) {
        return true;
        //return projectSdk.getSdkType() == CoqSdkType.INSTANCE;
    }

}
