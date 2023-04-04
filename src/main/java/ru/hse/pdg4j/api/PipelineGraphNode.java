package ru.hse.pdg4j.api;

import java.util.Collection;

public interface PipelineGraphNode {
    /**
     * Get the task inside the node
     *
     * @return task
     */
    PipelineTask<?> getTask();

    /**
     * Get all the children of the current node
     *
     * @return collection of child nodes
     */
    Collection<PipelineGraphNode> getChildren();
}
