package ru.hse.pdg4j.api;

import java.util.Collection;

public interface PipelineContext {
    /**
     * Get context of the task by context's class
     *
     * @param clazz of the context to retrieve
     * @param <T>   type of the context
     * @return context
     */
    <T extends PipelineTaskContext> T getContext(Class<T> clazz);

    /**
     * Get a task by its class
     *
     * @param clazz of the task to retrieve
     * @param <T>   type of the task
     * @return task
     */
    <T extends PipelineTask<?>> T getTask(Class<T> clazz);

    /**
     * Get shared context by its class
     *
     * @param clazz of the shared context to retrieve
     * @param <T>   type of the shared context
     * @return shared context
     */
    <T extends PipelineSharedContext> T getSharedContext(Class<T> clazz);

    /**
     * Set shared context
     *
     * @param context context to set
     * @param clazz   class of the context to set
     * @param <T>     type of the context to set
     */
    <T extends PipelineSharedContext> void setSharedContext(T context, Class<T> clazz);

    /**
     * Get tasks yet to be executed
     *
     * @return collection of tasks to be executed
     */
    Collection<PipelineTask<?>> getPending();

    /**
     * Get the current task of the pipeline execution
     *
     * @return current task
     */
    // One task per execution at least for now
    PipelineTask<?> getRunning();

    /**
     * Get all the executed tasks so far
     *
     * @return collection of executed tasks
     */
    Collection<PipelineTask<?>> getExecuted();
}
