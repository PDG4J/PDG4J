package ru.hse.pdg4j.api;

import java.util.Collection;

public interface PipelineGraphNode {
    PipelineTask<?> getTask();
    Collection<PipelineGraphNode> getChildren();
}
