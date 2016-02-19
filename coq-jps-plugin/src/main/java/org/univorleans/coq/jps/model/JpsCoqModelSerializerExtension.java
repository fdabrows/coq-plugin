package org.univorleans.coq.jps.model;

import com.intellij.openapi.util.JDOMUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.JpsGlobal;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.serialization.JpsGlobalExtensionSerializer;
import org.jetbrains.jps.model.serialization.JpsModelSerializerExtension;
import org.jetbrains.jps.model.serialization.library.JpsSdkPropertiesSerializer;
import org.jetbrains.jps.model.serialization.module.JpsModulePropertiesSerializer;
import org.univorleans.coq.jps.builder.CoqBuilder;
import org.univorleans.coq.jps.builder.CoqBuilderUtil;

import java.util.Collections;
import java.util.List;

/**
 * Created by dabrowski on 20/01/2016.
 */
public class JpsCoqModelSerializerExtension extends JpsModelSerializerExtension {

    public static final String COQ_SDK_TYPE_ID = "Coq SDK";


    @NotNull
    @Override
    public List<? extends JpsGlobalExtensionSerializer> getGlobalExtensionSerializers() {

        //CoqBuilderUtil.LOG.info("JpsCoqModelSerializerExtension.getGlobalExtensionSerializers");
        return Collections.singletonList(new JpsGlobalConfigurationSerializer());
    }

    private static class JpsGlobalConfigurationSerializer extends JpsGlobalExtensionSerializer {

        protected JpsGlobalConfigurationSerializer() {
            super("coq.xml", "CoqConfiguration");
            //CoqBuilderUtil.LOG.info("JpsCoqModelSerializerExtension#JpsGlobalConfigurationSerializer");

        }

        @Override
        public void loadExtension(@NotNull JpsGlobal global, @NotNull Element componentTag) {
            //CoqBuilderUtil.LOG.info("JpsGlobalConfigurationSerializer.loadExtension");
            for (Element option : JDOMUtil.getChildren(componentTag, "option")) {
                if ("coqPath".equals(option.getAttributeValue("name"))) {
                    CoqBuilder.coqPath = option.getAttributeValue("value");
                }
            }
        }

        @Override
        public void saveExtension(@NotNull JpsGlobal global, @NotNull Element componentTag) {
            //CoqBuilderUtil.LOG.info("JpsGlobalConfigurationSerializer.saveExtension");
        }
    }

        @NotNull
        @Override
        public List<? extends JpsModulePropertiesSerializer<?>> getModulePropertiesSerializers() {
            //CoqBuilderUtil.LOG.info("JpsGlobalConfigurationSerializer.getModulePropertiesSerializers");
            return Collections.singletonList(new JpsCoqModulePropertiesSerializer());
        }

        @NotNull
        @Override
        public List<? extends JpsSdkPropertiesSerializer<?>> getSdkPropertiesSerializers() {
            //CoqBuilderUtil.LOG.info("JpsGlobalConfigurationSerializer.getSdkPropertiesSerializers");
            return Collections.singletonList(new JpsCoqSdkPropertiesSerializer());
        }



    private static class JpsCoqSdkPropertiesSerializer extends JpsSdkPropertiesSerializer<JpsSimpleElement<JpsCoqSdkProperties>> {
        private static final String HOME_PATH = "homePath";

        public JpsCoqSdkPropertiesSerializer() {
            super(COQ_SDK_TYPE_ID, JpsCoqSdkType.INSTANCE);
            //CoqBuilderUtil.LOG.info("JpsGlobalConfigurationSerializer#JpsCoqSdkPropertiesSerializer");

        }

        @NotNull
        @Override
        public JpsSimpleElement<JpsCoqSdkProperties> loadProperties(@Nullable Element propertiesElement) {

            //CoqBuilderUtil.LOG.info("JpsCoqSdkPropertiesSerializer.loadProperties");
            String coqPath;

            if (propertiesElement != null) {
                Element parent = (Element) propertiesElement.getParent();
                coqPath = parent.getChild(HOME_PATH).getAttributeValue("value");
            } else {
                coqPath = null;
            }
            //CoqBuilderUtil.LOG.info("JpsCoqSdkPropertiesSerializer.loadProperties " + coqPath);
            return JpsElementFactory.getInstance().createSimpleElement(new JpsCoqSdkProperties(coqPath));
        }

        @Override
        public void saveProperties(@NotNull JpsSimpleElement<JpsCoqSdkProperties> properties, @NotNull Element element) {
            //CoqBuilderUtil.LOG.info("JpsCoqSdkPropertiesSerializer.saveProperties");
        }
    }

}
