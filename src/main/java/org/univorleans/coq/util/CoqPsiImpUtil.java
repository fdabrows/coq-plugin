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

package org.univorleans.coq.util;

import com.intellij.lang.ASTNode;
import org.univorleans.coq.psi.*;
import org.univorleans.coq.psi.CoqFile;

public class CoqPsiImpUtil {


    public static String getKey(CoqFile element) {
        return element.getName();
    }


    public static String getKey(CoqAssertion element) {
        ASTNode valueNode = element.getNode().findChildByType(CoqTypes.ASSERTION_KEYWORD);
        if (valueNode != null) {
            return valueNode.getText();
        } else {
            return null;
        }
    }

    public static String getValue(CoqAssertion element) {
        ASTNode valueNode = element.getNode().findChildByType(CoqTypes.ID);
        if (valueNode != null) {
            return valueNode.getText();
        } else {
            return null;
        }
    }

    public static String getName(CoqFixBody element) {
        ASTNode valueNode = element.getNode().findChildByType(CoqTypes.ID);
        if (valueNode != null) {
            return valueNode.getText();
        } else {
            return null;
        }
    }

    public static String getName(CoqIndBody element) {
        ASTNode valueNode = element.getNode().findChildByType(CoqTypes.ID);
        if (valueNode != null) {
            return valueNode.getText();
        } else {
            return null;
        }
    }

    public static String getName(CoqDefinition element) {
        ASTNode valueNode = element.getNode().findChildByType(CoqTypes.ID);
        if (valueNode != null) {
            return valueNode.getText();
        } else {
            return null;
        }
    }

}
