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

package org.univorleans.coq.toplevel;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dabrowski on 29/01/2016.
 */
public class CoqtopStack {

    private final List<CoqtopState> coqtopStates = new ArrayList<>();

    private final EventListenerList listeners = new EventListenerList();

    public void save(CoqtopState c){
        if (coqtopStates.get(0).proofCounter != 0 && c.proofCounter == 0)
            while (coqtopStates.get(0).proofCounter != 0)
                coqtopStates.remove(0);
        coqtopStates.add(0, c);
        fireCoqStateChanged();
    }

    public void restore(int counter){
        assert counter >= 1;
        while (coqtopStates.get(0).globalCounter != counter)
            coqtopStates.remove(0);
        fireCoqStateChanged();
    }

    public CoqtopState current(){
        return coqtopStates.get(0);
    }

    public boolean hasPrevious(){
        return coqtopStates.size() > 1;
    }

    public CoqtopState previous(){
        return coqtopStates.get(1);
    }

    public void addCoqStateListener(CoqtopStackListener listener){
        listeners.add(CoqtopStackListener.class, listener);
    }

    public void fireCoqStateChanged(){
        for (CoqtopStackListener listener : listeners.getListeners(CoqtopStackListener.class)){
            listener.coqStateChangee(current());
        }
    }
}
