package ru.hse.pdg4j.api;

import java.util.Collection;
import java.util.Collections;

public interface PipelineTask<T extends PipelineTaskContext> {
    /**
     * Get name of the task
     *
     * @return name
     */
    String getName();

    /**
     * Get context of the task
     *
     * @return context
     */
    T getContext();

    /**
     * Execute the task
     *
     * @param context context of the pipeline
     * @return result of task's execution
     */
    PipelineTaskResult run(PipelineContext context);

    /**
     * Get task's requirements.
     * Pipeline task order is formed the way (if possible) that before a task is executed,
     * all the requirements are executed as well
     *
     * @return list of requirement task classes
     */
    default Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return Collections.emptyList();
    }

    /**
     * Whether the task is blocking.
     * Failed blocking task stops the entire pipeline.
     * May work differently for checking tasks, please refer to the corresponding javadocs
     *
     * @see ru.hse.pdg4j.api.check.task.CheckPipelineTask
     *
     * @return whether the task is blocking
     */
    default boolean isBlocking() {
        return false;
    }
}
