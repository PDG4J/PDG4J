package ru.hse.pdg4j.api;

import java.util.Collection;

public interface PipelineTask<T extends PipelineTaskContext> {
    String getName();
    T getContext();

    PipelineTaskResult run(PipelineContext context);
    Collection<Class<? extends PipelineTask<?>>> getRequirements();
}
