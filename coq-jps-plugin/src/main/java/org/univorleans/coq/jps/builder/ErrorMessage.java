package org.univorleans.coq.jps.builder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dabrowski on 19/02/2016.
 */
public class ErrorMessage {

    final public String url;
    final public int line;
    final public int column;
    final public String message;

    private final Pattern pattern = Pattern.compile(
            "File \"([a-zA-Z0-9/]+.v)\", line ([0-9]+), characters ([0-9]+)-([0-9]+):(.*)");

    public ErrorMessage(List<String> lines) {
        if (lines.size() > 0) {
            Matcher matcher = pattern.matcher(lines.get(0));
            if (matcher.matches()) {
                url = matcher.group(1);
                line = Integer.parseInt(matcher.group(2));
                column = Integer.parseInt(matcher.group(3));
                if (lines.size() > 1) message = lines.get(1);
                else message = "No message";
            } else {
                url = null;
                line = -1;
                column = -1;
                String msg = "";
                for (String str : lines)
                    msg += str + "\n";
                message = msg;
            }
        } else {
            url = null;
            line = -1;
            column = -1;
            message = "Bad format error message";
        }
    }
}
