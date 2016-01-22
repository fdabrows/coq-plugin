package org.univorleans.coq.jps.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildTargetType;
import org.jetbrains.jps.incremental.BuilderService;
import org.jetbrains.jps.incremental.TargetBuilder;

import java.util.List;

/**
 * Created by dabrowski on 20/01/2016.
 */
public class CoqBuilderService extends BuilderService{

    @NotNull
    @Override
    public List<? extends BuildTargetType<?>> getTargetTypes() {
        System.out.println("CoqBuilderService.getTargetTypes");
        return null;
        //return ContainerUtil.newArrayList(CoqModuleBuildOrderTargetType.INSTANCE, CoqTargetType.INSTANCE);
    }

    @NotNull
    @Override
    public List<? extends TargetBuilder<?, ?>> createBuilders() {
        System.out.println("CoqBuilderService.createBuilders");
        return null;
        //return ContainerUtil.newArrayList(new CoqModuleBuildOrderBuilder(), new CoqBuilder(), new RebarBuilder());
    }
}
