package org.univorleans.coq.util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dabrowski on 24/02/2016.
 */
public class Messages {

    public static ResourceBundle bundle = java.util.ResourceBundle.getBundle("MessagesBundle", getLocale());

    public static String get(String key){
        return bundle.getString(key);
    }

    private static Locale getLocale(){
        return new Locale("en", "US");
    }

}
