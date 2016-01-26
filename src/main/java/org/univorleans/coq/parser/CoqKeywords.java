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

package org.univorleans.coq.parser;

import org.univorleans.coq.psi.CoqTypes;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class CoqKeywords {

    private static HashMap<String, IElementType> table = new HashMap<>();

    private static String[] commands = new String[] {
            "Abort", "About", "Add", "Admit", "Arguments",

            "Back", "BackTo", "Backtrack", "Bind",

            "Canonical", "Check", "Class", "Close", "Coercion", "Combined",
            "Compute", "Context", "CreateHintDb",

            "Declare", "Delimit", "Derived", "Drop",

            "End", "Eval", "Existential", "Existing", "Export", "Extract", "Extraction",

            "Focus", "Functional",

            "Generalizable", "Global", "Goal", "Grab", "Guarded",

            "Hint", "Indentity", "Implicit", "Import", "Include", "Infix", "Inline",
            "Inspect", "Instance",

            "Lemma", "Let", "Load", "Local", "Locate", "Ltac", "Module",
            "Next", "Notation",

            "Obligation", "Obligations", "Opaque", "Open",
            "Preterm", "Print", "Pwd",

            "Quit",

            "Record", "Recursive", "Remove", "Require", "Reserved", "Reset", "Restart", "Restore",

            "Save", "Scheme", "State", "Search", "SearchAbout", "SearchPattern", "SearchRewrite",
            "Section", "Separate", "Set", "Show", "Stategy", "Tactic", "Text", "Time",
            "Transparent", "Undo", "Unfocus", "Unset", "Variable", "Variables", "Whelp", "Write",
            "Equality",
            "Type",
            "Scope"
    };

    private static String[] keywords = new String[] {
            "forall",
            "exists",
            "let",
            "in",
            "struct",
            "fun",
            "return",
            "False",
            "True"
    };

    private static String[] tactics = new String[] {
            "abstract", "absurd", "admit", "apply", "assert",
            "assumption", "auto", "autorewrite", "autounfold",

            "case", "case_eq", "cbv", "change",
            "classical_left", "classical_left", "clear", "clearbody",
            "cofix", "compare", "compute", "congruence", "constr_eq",
            "constructor", "contradict", "contradiction", "cut",
            "cutrewrite",

            "decide", "decompose", "dependent", "destruct",
            "discriminate", "discR", "do", "double",

            "eapply","eassumption","eauto","ecase","econstructor","edstruct",
            "ediscriminate", "eelim", "eexact", "eexists", "einduction", "einjection",
            "eleft", "elim", "elimtype", "erewrite", "eright", "esimplify_eq", "esplit",
            "evar", "exact", "exfalso", "exists",

            "f_equal", "fail", "field", "field_simplify", "field_simplify_eq",
            "firts", "firstorder", "fix", "fold", "fourier", "functional",

            "generalize",

            "has_evar", "hnf",

            "idtac", "induction", "injection", "instantiate", "intro", "intros",
            "intuition", "inversion", "is_evar", "is_var",

            "lapply", "lazy", "left", "legacy", "lia", "lra",

            "move",

            "nia", "nsatz",

            "omega",

            "pattern", "pose", "progress", "psatz",

            "quote",

            "red", "refine", "remember", "reflexivity", "remember",
            "rename", "repeat", "revert", "rewrite", "right", "ring", "ring_simplify",
            "rtauto",

            "set", "setoid_replace", "simpl", "simple", "simplify_eq",
            "stepl", "stepr", "subst", "symmetry", "split",

            "tauto",  "timeout",  "transitivity",  "trivial",  "try",

            "unfold", "unify",

            "vm_compute",

            "proof", "field", "by", "now", "using",
            "eqn", "as"
    };

    static {

        for(int i =0; i < commands.length; i++)
            table.put(commands[i], CoqTypes.COMMAND);
        for(int i =0; i < keywords.length; i++)
            table.put(keywords[i], CoqTypes.KEYWORD);
        for(int i =0; i < tactics.length; i++)
            table.put(tactics[i], CoqTypes.TACTIC);

    }

    @Nullable
    public static IElementType getIElementType(String str){
        return table.get(str);
    }

}
