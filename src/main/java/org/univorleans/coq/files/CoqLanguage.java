package org.univorleans.coq.files;

import com.intellij.lang.Language;

/**
 * Created by dabrowski on 20/01/2016.
 */
public class CoqLanguage extends Language {

    public static final String ID = "Coq";
    public static final CoqLanguage INSTANCE = new CoqLanguage();

    private CoqLanguage(){
        super(ID);
    }
}
