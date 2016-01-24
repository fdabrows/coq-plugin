package org.univorleans.coq.jps.model;

import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.ex.JpsElementTypeWithDummyProperties;
import org.jetbrains.jps.model.module.JpsModuleType;

/**
 * Created by dabrowski on 22/01/2016.
 */
public class JpsCoqModuleType extends JpsElementTypeWithDummyProperties implements JpsModuleType<JpsDummyElement> {

    public static final JpsCoqModuleType INSTANCE = new JpsCoqModuleType();

}
