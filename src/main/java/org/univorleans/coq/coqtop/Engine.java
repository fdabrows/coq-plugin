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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.coqtop.errors.CoqtopPathError;
import org.univorleans.coq.coqtop.ui.MessageTextListener;
import org.univorleans.coq.coqtop.ui.ProofTextListener;
import org.univorleans.coq.coqtop.errors.InvalidCoqtopResponse;
import org.univorleans.coq.coqtop.errors.InvalidState;
import org.univorleans.coq.jps.model.JpsCoqSdkType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.Key;
import com.intellij.util.ui.UIUtil;
import org.univorleans.coq.coqtop.ui.Message;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.io.File;
import java.io.IOException;

public class Engine implements StackListener {

    //Notifications.Bus.notify(new Notification("coqtop", "Success", "start engine", NotificationType.INFORMATION));


    // With every editor, we associate an engine which is recorded
    // in the user data of the contained document. The key editorKey
    // is used to manage access to this editor through method
    // getEngine. At any time, at most one engine can be running,
    // this engine is stored in running

    private static final Key<Engine> editorKey = new Key<Engine>("engine");

    private static Engine running = null;

    private final EventListenerList listeners = new EventListenerList();

    private String messageText, proofText;

    // We use this stack to get offsets
    private final Stack coqtopStates;

    private Interface proofTopLevel;

    // View
    private Editor editor;

    CommandReader iterator;

    //returns the Engine associated with an editor.
    //@returns may return null
    //@assumes editor != null
    @Nullable
    public static Engine getEngine(@Nullable Editor editor) {

        if (editor == null) {
            showNoEditorSelectedDialog();
            return null;
        }

        try {
            Engine engine = editor.getUserData(editorKey);

            if (engine == null) {
                engine = new Engine(editor);
                editor.putUserData(editorKey, engine);
            }
            if (running != null && running != engine) {
                if (showEditorChange() == JOptionPane.CANCEL_OPTION) return null;
                running.stop();
            }
            running = engine;
            return engine;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        } catch (CoqtopPathError coqtopPathError) {
            coqtopPathError.printStackTrace();
            return null;
        } catch (InvalidCoqtopResponse invalidCoqtopResponse) {
            invalidCoqtopResponse.printStackTrace();
            return null;
        }
    }

    private Engine(@NotNull Editor editor) throws IOException, InvalidCoqtopResponse, CoqtopPathError {

        Sdk projectSdk = ProjectRootManager.getInstance(editor.getProject()).getProjectSdk();

        if (projectSdk == null) {
            showSdkError();
            throw new CoqtopPathError();
        }


        File coqtop = JpsCoqSdkType.getByteCodeInterpreterExecutable(projectSdk.getHomePath());

        ModuleManager manager = ModuleManager.getInstance(editor.getProject());

        VirtualFile currentFile = FileDocumentManager.getInstance().getFile(editor.getDocument());

        Module currentModule = Util.getModule(manager, currentFile);

        if (currentModule == null) {
            showNoModuleError(currentFile.getName());
            throw new IOException();
        }
        //TODO : Dans le cas d'un import, il n'y a pas de modulefile
        File base = new File(editor.getProject().getBasePath());

        File[] include = Util.getSourceRoots(currentModule);
//        VirtualFile vf =
//                editor.getProject().getBaseDir().getName() + File.separator + "production";

        Logger logger = Logger.getInstance(Engine.class);

        proofTopLevel = new Interface(editor.getProject(), coqtop, base, include);

        Response response = proofTopLevel.start();

        Stack stack = null;
        try {
            stack = new Stack(new State(response.globalCounter, response.proofCounter, 0));
        } catch (InvalidState invalidState) {

            invalidState.printStackTrace();
        }
        coqtopStates = stack;

        this.editor = editor;

        //iterator = new CommandReader(editor.getDocument());
        logger.info(proofTopLevel.toString());
        addMessageViewListener(Message.INSTANCE);
        addCoqStateListener(Message.INSTANCE);
        addProofViewListener(Message.INSTANCE);
        addCoqStateListener(this);

    }

    public boolean next() {

        try {
            int endOffset = CommandReader.hasNext(editor.getDocument(), coqtopStates.top().offset);

            if (endOffset < 0){
                setMessageText("No command to proceed.");
                return false;
            }
            String cmd = CommandReader.getCommand(editor.getDocument(),
                    new TextRange(coqtopStates.top().offset, endOffset));

            Response response = proofTopLevel.send(cmd);

            State newCoqState = new State(response.globalCounter, response.proofCounter, endOffset);

            if (newCoqState.globalCounter > coqtopStates.top().globalCounter)
                coqtopStates.save(newCoqState);
            else if (newCoqState.globalCounter < coqtopStates.top().globalCounter)
                coqtopStates.stepBack(newCoqState.globalCounter);
            else {
                setMessageText(response.message);
                return false;
            }

            if (newCoqState.mode() == State.GENERAL) {
                setMessageText(response.message + "\n\n" + response.info);
            } else {
                setProofText(response.message);
            }

            return true;

        } catch (InvalidCoqtopResponse invalidCoqtopResponse) {
            invalidCoqtopResponse.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InvalidState invalidState) {
            invalidState.printStackTrace();
            return false;
        }
    }

    public void undo() {

        try {
            if (coqtopStates.hasPreviousStep()) {
                coqtopStates.stepBack();
                Response response = proofTopLevel.send(Util.backTo(coqtopStates.top().globalCounter));
                setMessageText(response.message + "\n\n" + response.info);
            }
        } catch (InvalidCoqtopResponse invalidCoqtopResponse) {
            invalidCoqtopResponse.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidState invalidState) {
            invalidState.printStackTrace();
        }

    }

    public void use() {
        while (next()) ;
    }

    public void retract() {
        try {
            Response response = proofTopLevel.send(Util.backTo(1));

            coqtopStates.stepBack(response.globalCounter);

            if (response.proofCounter == 0)
                setMessageText(response.message);
            else setProofText(response.message);

        } catch (InvalidCoqtopResponse invalidCoqtopResponse) {
            invalidCoqtopResponse.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidState invalidState) {
            invalidState.printStackTrace();
        }
    }


    // Le moteur doit travailler a partir de la pile d'etats uniquement.
    // aussi bien pour avancer que pour reculer.
    public void gotoCursor() {
        try {
            int currentOffset = editor.getCaretModel().getOffset();
            if (coqtopStates.top().offset < currentOffset) {
                while (coqtopStates.top().offset < currentOffset) {
                    if (CommandReader.hasNext(editor.getDocument(), coqtopStates.top().offset) >= 0) {
                        if (!next()) break;
                    } else break;
                }
            } else if (coqtopStates.top().offset > currentOffset) {
                while (coqtopStates.top().offset > currentOffset) {
                    if (coqtopStates.hasPreviousStep()) {
                        coqtopStates.stepBack();
                        Response response = proofTopLevel.send(Util.backTo(coqtopStates.top().globalCounter));

/*                        int lastState = coqtopStates.previous().globalCounter;
                        Response response = proofTopLevel.send(Util.backTo(lastState));
                        coqtopStates.stepBack(response.prompt.getGlobalCounter());*/
                    } else break;
                }
            }
        } catch (InvalidState e) {
            e.printStackTrace();
        } catch (InvalidCoqtopResponse invalidCoqtopResponse) {
            invalidCoqtopResponse.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (proofTopLevel != null) {
                proofTopLevel.stop();
                proofTopLevel = null;
            }
            editor.getMarkupModel().removeAllHighlighters();
            editor.putUserData(editorKey, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMessageText(String str) {
        messageText = str;
        fireMessageViewChanged();
    }

    private void setProofText(String str) {
        proofText = str;
        fireProofViewChanged();
    }

    @Override
    public void coqStateChangee(Stack c) {
        editor.getMarkupModel().removeAllHighlighters();

        TextAttributes attr = new TextAttributes();
        attr.setBackgroundColor(UIUtil.getTreeSelectionBackground());
        attr.setForegroundColor(UIUtil.getTreeSelectionForeground());
        editor.getMarkupModel().addRangeHighlighter(
                0, c.top().offset, 3333, attr, HighlighterTargetArea.EXACT_RANGE);
        RangeMarker marker = editor.getDocument().getOffsetGuard(0);
        if (marker != null) editor.getDocument().removeGuardedBlock(marker);
        editor.getDocument().createGuardedBlock(0, c.top().offset);
        editor.getCaretModel().moveToOffset(c.top().offset);
        editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);


    }

    public void addCoqStateListener(StackListener listener) {

        coqtopStates.addCoqStateListener(listener);
    }

    public void addProofViewListener(ProofTextListener listener) {

        listeners.add(ProofTextListener.class, listener);
    }


    public void addMessageViewListener(MessageTextListener listener) {

        listeners.add(MessageTextListener.class, listener);
    }

    private void fireProofViewChanged() {
        for (ProofTextListener listener : listeners.getListeners(ProofTextListener.class)) {
            listener.proofViewChangee(proofText);
        }
    }

    private void fireMessageViewChanged() {
        for (MessageTextListener listener : listeners.getListeners(MessageTextListener.class)) {
            listener.messageViewChangee(messageText);
        }
    }

    private static void showNoEditorSelectedDialog() {
        JOptionPane.showConfirmDialog(null, "No editor selected", "No editor selected", JOptionPane.OK_OPTION);

    }

    private static int showEditorChange() {
        return JOptionPane.showConfirmDialog(null, "Editor change", "Editor change", JOptionPane.OK_CANCEL_OPTION);

    }

    private static void showSdkError() {
        String error_msg =
                "Cannot start coqtop: the sdk is not specified.\n" +
                        "Specifiy the sdk at Project Structure dialog";

        JOptionPane.showMessageDialog(null, error_msg, "SDK Error", JOptionPane.ERROR_MESSAGE);

    }

    private static void showNoModuleError(String file) {
        String error_msg =
                "File " + file + " does not belong to a module";

        JOptionPane.showMessageDialog(null, error_msg, "Module Error", JOptionPane.ERROR_MESSAGE);

    }
}

