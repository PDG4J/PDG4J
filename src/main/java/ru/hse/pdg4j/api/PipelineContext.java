package ru.hse.pdg4j.api;

import java.util.Collection;

public interface PipelineContext {
    <T extends PipelineTaskContext> T getContext(Class<T> clazz);
    <T extends PipelineTask<?>> T getTask(Class<T> clazz);

    <T extends PipelineSharedContext> T getSharedContext(Class<T> clazz);
    <T extends PipelineSharedContext> void setSharedContext(T context, Class<T> clazz);

    Collection<PipelineTask<?>> getPending();

    // One task per execution at least for now
    PipelineTask<?> getRunning();

    Collection<PipelineTask<?>> getExecuted();
}
