package ru.hse.pdg4j.api;

public interface FlowPipeline {
    String getName();

    void run(PipelineGraphNode root, ExecutionListener listener);
}
