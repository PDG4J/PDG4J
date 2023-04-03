package ru.hse.pdg4j.api;

import java.util.Collection;
import java.util.Collections;

public interface PipelineTask<T extends PipelineTaskContext> {
    String getName();
    T getContext();

    PipelineTaskResult run(PipelineContext context);
    default Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return Collections.emptyList();
    }

    default boolean isBlocking() {
        return false;
    }
}
