package ru.hse.pdg4j.api;

public interface PipelineExecutionListener {
    void onComplete(PipelineTask<?> pipelineTask, PipelineTaskResult result, PipelineGraphNode current);
    void onException(PipelineTask<?> pipelineTask, PipelineGraphNode current, Exception e);
}
