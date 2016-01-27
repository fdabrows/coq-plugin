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

import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.univorleans.coq.errors.CoqtopPathError;
import org.univorleans.coq.errors.InvalidCoqtopResponse;
import org.univorleans.coq.errors.NoCoqProcess;
import org.univorleans.coq.jps.model.JpsCoqSdkType;
import org.univorleans.coq.listeners.MessageTextListener;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.ui.UIUtil;
import org.univorleans.coq.toolWindows.CoqtopMessageView;
import org.univorleans.coq.listeners.ProofTextListener;
import org.univorleans.coq.util.FilesUtil;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CoqtopEngine implements CoqStateListener {

    // With every editor, we associate an engine which is recorded
    // in the user data of the contained document. The key editorKey
    // is used to manage access to this editor through method
    // getEngine. At any time, at most one engine can be running,
    // this engine is stored in running

    private static final Key<CoqtopEngine> editorKey = new Key<CoqtopEngine>("engine");
    private static CoqtopEngine running = null;


    private static void showNoEditorSelectedDialog (){
        JOptionPane.showConfirmDialog(null, "No editor selected", "No editor selected", JOptionPane.OK_OPTION);

    }

    private static int showEditorChange(){
        return JOptionPane.showConfirmDialog(null, "Editor change", "Editor change", JOptionPane.OK_CANCEL_OPTION);

    }

    //returns the CoqtopEngine associated with an editor.
    //@returns may return null
    //@assumes editor != null
    @Nullable
    public static CoqtopEngine getEngine(@Nullable  Editor editor) {

        if (editor == null) {
            showNoEditorSelectedDialog();
            return null;
        }

        try{
            CoqtopEngine coqtopEngine = editor.getUserData(editorKey);
            if (coqtopEngine == null){
                coqtopEngine = new CoqtopEngine(editor);
                editor.putUserData(editorKey, coqtopEngine);
            }
            else if (running != null && running != coqtopEngine) {
                if (showEditorChange() == JOptionPane.CANCEL_OPTION) return null;
                running.stop();
            }
            running = coqtopEngine;
            return coqtopEngine;
        } catch (IOException ioException){
            return null;
        } catch (CoqtopPathError coqtopPathError) {
            coqtopPathError.printStackTrace();
            JOptionPane.showMessageDialog(null, "Set coq path!");
            return null;
        } catch (InvalidCoqtopResponse invalidCoqtopResponse) {
            invalidCoqtopResponse.printStackTrace();
            return null;
        }
    }

    private final EventListenerList listeners = new EventListenerList();

    // TODO : find another way to record listeners and remove these two fields
    public static ProofTextListener proofView;
    public static CoqtopMessageView messageView;

    private String messageText, proofText;
    private final List<CoqState> coqStates = new ArrayList<>();
    private CoqtopInterface proofTopLevel;


    // View
    private Editor editor;

    private CoqtopEngine(@NotNull Editor editor) throws IOException, InvalidCoqtopResponse, CoqtopPathError {

        CoqTopLevelResponse response;

        this.editor = editor;

        Sdk projectSdk = ProjectRootManager.getInstance(editor.getProject()).getProjectSdk();

        if (projectSdk == null){
            String error_msg =
                    "Cannot start coqtop: the sdk is not specified.\n"+
                            "Specifiy the sdk at Project Structure dialog";


            JOptionPane.showMessageDialog(null,error_msg, "SDK Error",JOptionPane.ERROR_MESSAGE);
        }

        File coqtop = JpsCoqSdkType.getByteCodeInterpreterExecutable(projectSdk.getHomePath());


        Project project = editor.getProject();
        ModuleManager manager = ModuleManager.getInstance(editor.getProject());
        VirtualFile currentFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
        Module currentModule = null;

        Module[] modules = manager.getModules();
        for (Module module : modules) {
            if (module.getModuleScope().accept(currentFile)){
                currentModule = module;
                break;
            }
        }

        ModuleRootManager root = ModuleRootManager.getInstance(currentModule);
        VirtualFile[] roots = root.getSourceRoots();
        List<File> include = new ArrayList<>();
        for (VirtualFile vfile : roots){
            include.add(new File(vfile.getPath()));
            for (VirtualFile vfile2 : FilesUtil.getSubdirs(vfile))
                include.add(new File(vfile2.getPath()));
        }

        File base = new File(currentModule.getModuleFile().getParent().getPath());

        proofTopLevel = new CoqtopInterface(coqtop, base, include.toArray(new File[0]));

        response = proofTopLevel.start();

        coqStates.add(new CoqState(response.prompt, 0));


        // TODO : FIND ANOTHER WAY TO RECORD LISTENERS
        //addCoqStateListener(this);
        //if (proofView != null) addProofViewListener(proofView);
        //if (messageView != null) {
        //    addMessageViewListener(messageView);
        //    addCoqStateListener(messageView);
        //}
    }

    private void setMessageText(String str){
        messageText = str;
        fireMessageViewChanged();
    }

    private void setProofText(String str){
        proofText = str;
        fireProofViewChanged();
    }

    public void saveCoqState(CoqState c){
        if (coqStates.get(0).proofCounter != 0 && c.proofCounter == 0)
            while (coqStates.get(0).proofCounter != 0)
                coqStates.remove(0);
        coqStates.add(0, c);
        fireCoqStateChanged();
    }

    public void restoreCoqState(int counter){
        assert counter >= 1;
        while (coqStates.get(0).globalCounter != counter)
            coqStates.remove(0);
        fireCoqStateChanged();
    }

    public CoqState currentCoqState(){
        return coqStates.get(0);
    }

    private static boolean isCommand (String str){
        int cpt = 0;
        for (int i =0; i < str.length() - 1; i++){
            if (str.charAt(i) == '(' && str.charAt(i+1) == '*') cpt++;
            if (str.charAt(i) == '*' && str.charAt(i+1) == ')') cpt--;
        }
        return (cpt == 0);
    }


    public boolean next() {

        Document document = editor.getDocument();

        String txt = document.getText();

        int startOffset = currentCoqState().offset;
        int endOffset = txt.indexOf('.', startOffset) + 1;

        if (endOffset <= 0) {
            setMessageText("No command to proceed");
            setProofText("");
            return false;
        }

        String cmd = document.getText(new TextRange(startOffset, endOffset)).replace('\n', ' ');
        int searchOffset = startOffset;
        while (true) {
            boolean b = false;
            if (endOffset < document.getText().length()){
                b = b || document.getText().charAt(endOffset) == '\n';
                b = b || document.getText().charAt(endOffset) == '\r';
                b = b || document.getText().charAt(endOffset) == '\t';
                b = b || document.getText().charAt(endOffset) == ' ';
            }
            if (isCommand(cmd) && b) break;

            searchOffset = endOffset + 1;
            endOffset = txt.indexOf('.', searchOffset) + 1;
            if (endOffset <= 0) {
                setMessageText("No command to proceed");
                setProofText("");
                return false;
            }
            cmd = document.getText(new TextRange(startOffset, endOffset)).replace('\n', ' ');
        }
        CoqTopLevelResponse response = null;
        try {
            response = proofTopLevel.send(cmd);
        } catch (InvalidCoqtopResponse invalidCoqtopResponse) {
            invalidCoqtopResponse.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CoqTopLevelPrompt prompt = response.prompt;

        if (prompt.getGlobalCounter() == currentCoqState().globalCounter) {

        }
            if (prompt.getGlobalCounter() > currentCoqState().globalCounter) {
            saveCoqState(new CoqState(prompt, endOffset));

        } else if (prompt.getGlobalCounter() < currentCoqState().globalCounter){
            restoreCoqState(prompt.getGlobalCounter());
        } else {
            // TODO : Put in red
            setMessageText(response.message());
            setProofText("");
            return false;
        }
        if (response.prompt.getProofCounter() == 0) { // TODO : METHODE DE TEST KIND DANS RESPONSE
            setMessageText(response.message());
            setProofText("");
        }
        else {
            setMessageText("");
            setProofText(response.message());
        }
        return true;
    }


    public void undo() throws NoCoqProcess, IOException, InvalidCoqtopResponse {

        if (coqStates.size() == 1)  return;

        int lastState = coqStates.get(1).globalCounter;
        CoqTopLevelResponse response = proofTopLevel.send(CoqtopUtil.backTo(lastState));
        if (response.prompt.getProofCounter() == 0) // TODO : METHODE DE TEST KIND DANS RESPONSE
        // TODO : MAUVAIS CRITERE LE COMPTEUR PEUT ETRE nul EN CAS D'erreur dans une preuve
            setMessageText(response.message());
        else setProofText(response.message());
        restoreCoqState(response.prompt.getGlobalCounter());
    }

    public void use() throws NoCoqProcess, IOException, InvalidCoqtopResponse {
        while (next());
    }

    public void retract() throws NoCoqProcess, IOException, InvalidCoqtopResponse {
        CoqTopLevelResponse response = proofTopLevel.send(CoqtopUtil.backTo(1));
        if (response.prompt.getProofCounter() == 0) // TODO : METHODE DE TEST KIND DANS RESPONSE
            setMessageText(response.message());
        else setProofText(response.message());
        restoreCoqState(response.prompt.getGlobalCounter());
    }

    public void gotoCursor(){

    }

    public void stop() throws IOException {
        proofTopLevel.stop();
        editor.getMarkupModel().removeAllHighlighters();
        editor.getDocument().putUserData(editorKey, null);
    }



    @Override
    public void coqStateChangee(CoqState c) {
        TextAttributes attr = new TextAttributes();
        attr.setBackgroundColor(UIUtil.getTreeSelectionBackground());
        attr.setForegroundColor(UIUtil.getTreeSelectionForeground());
        editor.getMarkupModel().removeAllHighlighters();
        editor.getMarkupModel().addRangeHighlighter(
                0,c.offset, 3333, attr, HighlighterTargetArea.EXACT_RANGE);
        RangeMarker marker = editor.getDocument().getOffsetGuard(0);
        if (marker != null) editor.getDocument().removeGuardedBlock(marker);
        editor.getDocument().createGuardedBlock(0, c.offset);
    }

    public void addCoqStateListener(CoqStateListener listener){
        listeners.add(CoqStateListener.class, listener);
    }

    public void addProofViewListener(ProofTextListener listener){
        listeners.add(ProofTextListener.class, listener);
    }


    public void addMessageViewListener(MessageTextListener listener){
        listeners.add(MessageTextListener.class, listener);
    }

    private void fireProofViewChanged(){
        for (ProofTextListener listener : listeners.getListeners(ProofTextListener.class)){
            listener.proofViewChangee(proofText);
        }
    }

    private void fireMessageViewChanged(){
        for (MessageTextListener listener : listeners.getListeners(MessageTextListener.class)){
            listener.messageViewChangee(messageText);
        }
    }

    public void fireCoqStateChanged(){
        for (CoqStateListener listener : listeners.getListeners(CoqStateListener.class)){
            listener.coqStateChangee(currentCoqState());
        }
    }
}

