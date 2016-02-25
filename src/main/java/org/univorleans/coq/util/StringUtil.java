package org.univorleans.coq.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by dabrowski on 23/02/2016.
 */
public class StringUtil {

    public static List<String> readAll(BufferedReader reader) throws IOException {
        List<String> result = new ArrayList<>();
        String str;
        while ((str = reader.readLine()) != null) result.add(str);
        return result;
    }

    public static String stringConcat(Collection<String> l, String sep) {

        String result = "";
        for (String str : l) result += str + sep;
        return result;
    }

    public static String stringConcat(String[] l, String sep) {

        String result = "";
        for (String str : l) result += str + sep;
        return result;
    }


}
