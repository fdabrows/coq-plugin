package org.univorleans.coq;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by dabrowski on 20/01/2016.
 */
public class testAction extends AnAction{
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        System.out.println("test");
    }
}
