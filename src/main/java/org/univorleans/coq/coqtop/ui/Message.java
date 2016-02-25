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

package org.univorleans.coq.coqtop.ui;

import org.univorleans.coq.coqtop.Stack;
import org.univorleans.coq.coqtop.StackListener;

/**
 * Created by dabrowski on 28/01/2016.
 */
public class Message implements MessageTextListener, StackListener, ProofTextListener {

    public static final Message INSTANCE = new Message();

    private CoqtopMessageView messageView;
    private CoqtopProofView proofView;

    private Message(){};

    @Override
    public void messageViewChangee(String txt) {
        if (messageView!= null) messageView.setText(txt);
    }

    @Override
    public void coqStateChangee(Stack c) {
        //if (messageView!= null)
        //messageView.setInfoText(c.toString());
    }

    @Override
    public void proofViewChangee(String txt) {
        if (proofView != null) proofView.setText(txt);
    }

    public void setMessaveView(CoqtopMessageView messageView){
        this.messageView = messageView;
    }

    public void setProofView(CoqtopProofView proofView){
        this.proofView = proofView;
    }

}
