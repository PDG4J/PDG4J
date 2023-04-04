package ru.hse.pdg4j.api;

public interface ExecutionListener {
    /**
     * Handle task completion
     *
     * @param pipelineTask completed task
     * @param result       result of the task
     * @param current      current node of the pipeline graph
     * @param context      context of the pipeline
     */
    default void onComplete(PipelineTask<?> pipelineTask, PipelineTaskResult result, PipelineGraphNode current, PipelineContext context) {
    }

    /**
     * Handle task exception
     *
     * @param pipelineTask task during which the exception has been thrown
     * @param current      current node of the pipeline graph
     * @param e            thrown exception
     * @param context      context of the pipeline
     */
    default void onException(PipelineTask<?> pipelineTask, PipelineGraphNode current, Exception e, PipelineContext context) {
    }

    /**
     * Handle pipeline finish
     *
     * @param context context of the pipeline
     */
    default void onFinish(PipelineContext context) {
    }

    /**
     * Combine execution listeners in sequential order
     *
     * @param listeners to combine
     * @return a listener combining all the passed listeners in sequential order
     */
    static ExecutionListener combine(ExecutionListener... listeners) {
        return new ExecutionListener() {
            @Override
            public void onComplete(PipelineTask<?> pipelineTask,
                                   PipelineTaskResult result,
                                   PipelineGraphNode current,
                                   PipelineContext context) {
                for (ExecutionListener listener : listeners) {
                    listener.onComplete(pipelineTask, result, current, context);
                }
            }

            @Override
            public void onException(PipelineTask<?> pipelineTask,
                                    PipelineGraphNode current,
                                    Exception e,
                                    PipelineContext context) {
                for (ExecutionListener listener : listeners) {
                    listener.onException(pipelineTask, current, e, context);
                }
            }

            @Override
            public void onFinish(PipelineContext context) {
                for (ExecutionListener listener : listeners) {
                    listener.onFinish(context);
                }
            }
        };
    }
}
