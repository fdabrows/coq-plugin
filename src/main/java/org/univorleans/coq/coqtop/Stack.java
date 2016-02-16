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

import org.univorleans.coq.coqtop.errors.InvalidState;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Stack is stack of CoqTopStates Objects. It denotes
 * the internal state of a Engine Object. A Stack
 * contains at least one state, the initial state passed to its
 * constructor.
 * <p>
 * Instances of the class StackListener can register as
 * listeners over stack changes.
 *
 * @author F. Dabrowski
 * @since v1.0
 */

public class Stack {

    /**
     * A list to store State objects.
     *
     * @see Stack#save(State)
     * @see Stack#stepBack(int)
     * @see Stack#top()
     * @see Stack#hasPreviousStep()
     * @see Stack#previous()
     */
    private final List<State> states = new ArrayList<>();

    /**
     * @see Stack#save(State)
     * @see Stack#stepBack(int)
     * @see Stack#addCoqStateListener(StackListener)
     * @see Stack#fireCoqStateChanged()
     */
    private final EventListenerList listeners = new EventListenerList();

    /**
     * @param c the initial state which must be tagged as general
     * @throws InvalidState i the state is not tagged as general
     */
    public Stack(State c) throws InvalidState {

        if (c.mode() == State.GENERAL) push(c);
        else throw new InvalidState();
    }

    /**
     * @param c A State to push on the stack, following states memory
     *          of Coq proof states are erased when entering general mode.
     */
    public void save(State c){

        try {
            if (c.mode() == State.GENERAL)
                while (top().mode() == State.PROOF) pop();
            push(c);
            fireCoqStateChanged();
        } catch (InvalidState e){
            e.printStackTrace();
        }
    }

    /**
     * @return true if the stack contains at least two elements,
     * false otherwise
     */

    public boolean hasPreviousStep() {

        return states.size() > 1;
    }

    /**
     * Restore the previous state
     *
     * @param
     * @throws InvalidState if no such state exists
     */
    public void stepBack() throws InvalidState {

        if (hasPreviousStep()) {
            int globalCounter = previous().globalCounter;
            while (top().globalCounter != globalCounter) pop();
            fireCoqStateChanged();
        } else throw new InvalidState();

    }

    /**
     * Restore a previous state by its global counter.
     *
     * @param globalCounter the globalCounter of the state to restore
     * @throws InvalidState if no such state exists
     */
    public void stepBack(int globalCounter) throws InvalidState {

        boolean exists = false;
        for (State state : states) {
            if (globalCounter == state.globalCounter) {
                exists = true;
                break;
            }
        }
        if (!exists) throw new InvalidState();
        while (top().globalCounter != globalCounter) pop();
        fireCoqStateChanged();
    }


    /**
     * @return the topmost state
     */
    public State top(){

        return states.get(0);
    }

    /**
     */

    @Override
    public String toString() {

        String msg = "";
        Iterator it = states.iterator();

        while (it.hasNext()) msg += it.next().toString();
        return msg;
    }

    /**
     */
    public void addCoqStateListener(StackListener listener) {

        listeners.add(StackListener.class, listener);
    }

    private void push(State c) {
        states.add(0, c);
    }

    private void pop() throws InvalidState {

        try {
            states.remove(0);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidState();
        }
    }

    private State previous() throws InvalidState {

        if (states.size() > 1) return states.get(1);
        else throw new InvalidState();
    }

    private void fireCoqStateChanged() throws InvalidState {

        for (StackListener listener : listeners.getListeners(StackListener.class)) {
            listener.coqStateChangee(this);
        }
    }
}
