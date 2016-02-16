/*
 * IntelliJ-coqplugin  / Plugin IntelliJ for Coq
 * Copyright (c) 2016 F. Dabrowski
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

package org.univorleans.coq.coqtop;

/**
 * Represents both the internal state of coqtop and the offset
 * of the current editor
 */

public class State {

    public static int GENERAL = 0;
    public static int PROOF = 1;

    /**
     *
     */
    public final int globalCounter, proofCounter, offset;

    /**
     *
     */
    public State(int globalCounter, int proofCounter, int offset){

        this.globalCounter = globalCounter;
        this.proofCounter = proofCounter;
        this.offset = offset;
    }

    /**
     *
     * @return
     */
    public int mode(){
        return offset == 0?GENERAL:PROOF;
    }

    /**
     *
     */
    public String toString(){
        return "(" + globalCounter +"|" + proofCounter+")";
    }

}
