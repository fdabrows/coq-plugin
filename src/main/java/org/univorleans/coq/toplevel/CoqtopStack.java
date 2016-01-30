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

import org.univorleans.coq.errors.InvalidState;

import javax.naming.directory.InvalidSearchControlsException;
import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dabrowski on 29/01/2016.
 */
public class CoqtopStack {

    private final List<CoqtopState> coqtopStates = new ArrayList<>();

    private final EventListenerList listeners = new EventListenerList();

    public void save(CoqtopState c) throws InvalidState {
        if (c.proofCounter == 0)
            while (coqtopStates.size() > 0 && top().proofCounter != 0) pop();
        push(c);
        fireCoqStateChanged();
    }

    public void restore(int counter) throws InvalidState {
        while (coqtopStates.size() > 0 && top().globalCounter != counter) pop();
        fireCoqStateChanged();
    }

    public boolean hasPrevious(){
        return coqtopStates.size() > 1;
    }

    public CoqtopState previous() throws InvalidState {
        try {
            return coqtopStates.get(1);
        } catch (IndexOutOfBoundsException e){
            throw new InvalidState();
        }
    }

    public void push(CoqtopState c) throws InvalidState{
        try{
            coqtopStates.add(0,c);
        } catch (IndexOutOfBoundsException e){
            throw new InvalidState();
        }
    }

    public CoqtopState top() throws InvalidState {
        try {
            return coqtopStates.get(0);
        } catch (IndexOutOfBoundsException e){
            throw new InvalidState();
        }
    }

    public void pop() throws InvalidState{
        try{
            coqtopStates.remove(0);
        } catch (IndexOutOfBoundsException e){
            throw new InvalidState();
        }
    }

    public void addCoqStateListener(CoqtopStackListener listener){
        listeners.add(CoqtopStackListener.class, listener);
    }

    public void fireCoqStateChanged() throws InvalidState {
        for (CoqtopStackListener listener : listeners.getListeners(CoqtopStackListener.class)){
                listener.coqStateChangee(top());

        }
    }

}
