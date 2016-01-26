/*
 * IntelliJ-coqplugin  / Plugin IntelliJ for Coq
 * Copyright (c) 2016
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.univorleans.coq.actions;

import com.intellij.openapi.actionSystem.*;

import com.intellij.openapi.vfs.VirtualFile;
import org.univorleans.coq.files.CoqFileType;


import org.jetbrains.jps.incremental.BuilderService;
import org.jetbrains.jps.incremental.TargetBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class GotoAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {

        ServiceLoader<BuilderService> s = ServiceLoader.load(BuilderService.class);
        Iterator<BuilderService> iter = s.iterator();
        System.out.println(iter.hasNext());
        while(iter.hasNext()){
            System.out.println("AAA");
            List<? extends TargetBuilder<?, ?>> l = iter.next().createBuilders();
            System.out.println(iter.next().toString());
        }

        //BuilderService bs = ServiceManager.getService(BuilderService.class);

        //     System.out.println(bs.toString());
/*            Project p = e.getProject();
            Editor editor = FileEditorManager.getInstance(p).getSelectedTextEditor();
            if (editor == null) return;
            CoqtopEngine coqtopEngine = CoqtopEngine.getEngine(editor);
            if (coqtopEngine != null) coqtopEngine.next();*/

    }

    @Override
    public void update(final AnActionEvent event)
    {
        DataContext dataContext = event.getDataContext();
        VirtualFile file = PlatformDataKeys.VIRTUAL_FILE.getData(dataContext);
        boolean enabled = (file != null && file.getFileType() == CoqFileType.INSTANCE);
        Presentation presentation = event.getPresentation();
        presentation.setVisible(true);
        presentation.setEnabled(enabled);
    }
}
