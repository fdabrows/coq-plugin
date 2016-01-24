package org.univorleans.coq.jps.model;

import org.jetbrains.annotations.Nullable;

/**
 * Created by dabrowski on 24/01/2016.
 */
public class JpsCoqSdkProperties {

    @Nullable
    private final String coqPath;

    public JpsCoqSdkProperties(@Nullable String ghcPath) {
        this.coqPath = ghcPath;
    }


    @Nullable
    public String getGhcPath() {
        return coqPath;
    }
}
