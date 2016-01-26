package org.univorleans.coq.compilation;

import org.jetbrains.annotations.NotNull;
import org.univorleans.coq.jps.builder.CoqProjectDependencies;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dabrowski on 25/01/2016.
 */
public class CoqdependenciesUtil {

    public static CoqProjectDependencies extractDependencies(@NotNull List<String> coqdepOutput){

        ConcurrentMap<String, List<String>> map = new ConcurrentHashMap<>();

        List<String> dep = new ArrayList<>();
        for (String str2 : coqdepOutput) {
            String[] parts = str2.split(":");
            String[] left = parts[0].split(" ");
            String[] right = parts[1].split(" ");
            Pattern pattern = Pattern.compile("[^.]*.vo");
            for (String a : left) {
                Matcher matcher1 = pattern.matcher(a);
                if (matcher1.matches()) {
                    for (String b : right) {
                        Matcher matcher2 = pattern.matcher(b);
                        if (matcher2.matches()) {
                            String path1 = a.substring(0, a.length() - 1);
                            String path2 = b.substring(0, b.length() - 1);
                            if (map.containsKey(path1))
                                map.get(path1).add(path2);
                            else map.put(path1, Collections.singletonList(path2));
                        }
                    }
                }
            }
        }
        return new CoqProjectDependencies(map);
    }
}
