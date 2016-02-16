package org.univorleans.coq.config;

import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.ex.JpsElementTypeWithDummyProperties;
import org.jetbrains.jps.model.module.JpsModuleSourceRootType;

/**
 * Created by dabrowski on 14/02/2016.
 */
public class CoqIncludeSourceRootType extends JpsElementTypeWithDummyProperties implements JpsModuleSourceRootType<JpsDummyElement> {
    public static final CoqIncludeSourceRootType INSTANCE = new CoqIncludeSourceRootType();

    private CoqIncludeSourceRootType() {
    }

}
