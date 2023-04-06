package ru.hse.pdg4j.api;

public interface FlowPipeline {
    /**
     * Get name of the pipeline
     *
     * @return name
     */
    String getName();

    /**
     * Execute the pipeline
     *
     * @param root     root node of pipeline graph
     * @param listener listener of pipeline execution
     */
    void run(PipelineGraphNode root, ExecutionListener listener);
}
