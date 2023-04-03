package ru.hse.pdg4j.api;

public interface ExecutionListener {
    default void onComplete(PipelineTask<?> pipelineTask, PipelineTaskResult result, PipelineGraphNode current, PipelineContext context) {

    }

    default void onException(PipelineTask<?> pipelineTask, PipelineGraphNode current, Exception e, PipelineContext context) {

    }

    default void onFinish(PipelineContext context) {

    }

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
