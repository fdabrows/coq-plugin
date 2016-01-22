package org.univorleans.coq.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.jps.incremental.BuilderService;
import org.univorleans.coq.jps.builder.CoqBuilderService;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by dabrowski on 22/01/2016.
 */
public class TestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        System.out.println("Test"); System.out.flush();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        //ClassLoader cl = TestAction.class.getClassLoader();
        CoqBuilderService bs = new CoqBuilderService();
        try {
            Enumeration<URL> urls = cl.getResources("META-INF/services/org.jetbrains.jps.incremental.BuilderService");
            while (urls.hasMoreElements()){
                System.out.println(urls.nextElement().getPath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        ServiceLoader<BuilderService> bds  = ServiceLoader.load(BuilderService.class);
        Iterator<BuilderService> it = bds.iterator();
        while(it.hasNext())
            System.out.println(it.next());

    }
}

// services -> META-INF du build principal
// ERLANG
// - META-INF/plugin.xml
// - Classes/...
// - lib/jps-plugin.jar
// jps-plugin.jar
// - META-INF
// -- services
// -- org