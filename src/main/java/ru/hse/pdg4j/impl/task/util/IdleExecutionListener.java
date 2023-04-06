package ru.hse.pdg4j.impl.task.util;

import ru.hse.pdg4j.api.*;

public class IdleExecutionListener implements ExecutionListener {
    @Override
    public void onComplete(PipelineTask<?> pipelineTask, PipelineTaskResult result, PipelineGraphNode current, PipelineContext context) {
    }

    @Override
    public void onException(PipelineTask<?> pipelineTask, PipelineGraphNode current, Exception e, PipelineContext context) {
    }

    @Override
    public void onFinish(PipelineContext context) {
    }
}
