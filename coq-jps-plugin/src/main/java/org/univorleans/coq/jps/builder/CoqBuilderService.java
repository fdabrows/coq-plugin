package org.univorleans.coq.jps.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.incremental.BuilderService;
import org.jetbrains.jps.incremental.ModuleLevelBuilder;
import java.util.Collections;
import java.util.List;

/**
 * Created by dabrowski on 20/01/2016.
 */

public class CoqBuilderService extends BuilderService{


    @NotNull
    @Override
    public List<? extends ModuleLevelBuilder> createModuleLevelBuilders() {

        return Collections.singletonList(new CoqBuilder());
    }
}
