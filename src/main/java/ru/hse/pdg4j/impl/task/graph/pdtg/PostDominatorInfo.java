package ru.hse.pdg4j.impl.task.graph.pdtg;

import fr.inria.controlflow.ControlFlowGraph;
import fr.inria.controlflow.ControlFlowNode;

public class PostDominatorInfo {
    private ConditionalGraph graph;
    private ControlFlowNode start;
    private ControlFlowNode end;

    public PostDominatorInfo(ConditionalGraph graph, ControlFlowNode start, ControlFlowNode end) {
        this.graph = graph;
        this.start = start;
        this.end = end;
    }

    public ConditionalGraph getControlFlowGraph() {
        return  this.graph;
    }

    public ControlFlowNode getStart() {
        return this.start;
    }

    public ControlFlowNode getEnd() {
        return this.end;
    }
}
