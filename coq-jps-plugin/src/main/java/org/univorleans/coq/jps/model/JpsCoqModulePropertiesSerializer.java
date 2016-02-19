package org.univorleans.coq.jps.model;

import org.jdom.Element;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.impl.JpsDummyElementImpl;
import org.jetbrains.jps.model.impl.JpsSimpleElementImpl;
import org.jetbrains.jps.model.serialization.module.JpsModulePropertiesSerializer;
import org.univorleans.coq.jps.builder.CoqBuilderUtil;

/**
 * Created by dabrowski on 24/01/2016.
 */
public class JpsCoqModulePropertiesSerializer extends JpsModulePropertiesSerializer<JpsDummyElement> {

    public JpsCoqModulePropertiesSerializer() {
        super(JpsCoqModuleType.INSTANCE, "COQ_MODULE", "Coq.ModuleBuildProperties");
        //CoqBuilderUtil.LOG.info("JpsCoqModulePropertiesSerializer");

    }

    @Override
    public JpsDummyElement loadProperties(Element componentElement) {
        //CoqBuilderUtil.LOG.info("JpsCoqModulePropertiesSerializer.loadPropertis");
        return new JpsDummyElementImpl();
    }

    @Override
    public void saveProperties(JpsDummyElement properties, Element componentElement) {
        //CoqBuilderUtil.LOG.info("JpsCoqModulePropertiesSerializer.saveProperties");
    }

}
