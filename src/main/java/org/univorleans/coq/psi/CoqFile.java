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

package org.univorleans.coq.psi;

import org.univorleans.coq.files.CoqFileType;
import org.univorleans.coq.files.CoqLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by Dabrowski on 27/11/2015.
 */
public class CoqFile extends PsiFileBase {

    public CoqFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CoqLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return CoqFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}
