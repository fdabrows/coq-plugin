package org.univorleans.coq.compilation;

import org.jetbrains.annotations.NotNull;
import org.univorleans.coq.jps.builder.CoqFileDependencies;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dabrowski on 25/01/2016.
 */
public class CoqdependenciesUtil {

    @NotNull
    public static CoqFileDependencies extractDependency(@NotNull String path, @NotNull List<String> coqdepOutput){
        List<String> dep = new ArrayList<>();
        for (String str2 : coqdepOutput) {
            String[] parts = str2.split(":");
            String[] left = parts[0].split(" ");
            String[] right = parts[1].split(" ");
            Pattern pattern = Pattern.compile("[^.]*.vo");
            for (String a : left) {
                if (path.equals(a.substring(0, a.length() - 1))) {
                    for (String b : right) {
                        Matcher matcher2 = pattern.matcher(b);
                        if (matcher2.matches()){
                            dep.add(b.substring(0, b.length() - 1));
                        }
                    }
                }
            }
        }
        return new CoqFileDependencies(path, dep);
    }
}
