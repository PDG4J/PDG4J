package ru.hse.pdg4j.impl.task.graph.pdtg;

import fr.inria.controlflow.ControlFlowNode;

public class ConditionalEdge {
    private ConditionalGraphNode source;
    private ConditionalGraphNode target;
    private ConditionalEdgeType type;
    private boolean isBackEdge = false;

    public ConditionalEdge(ConditionalGraphNode source, ConditionalGraphNode target, ConditionalEdgeType type) {
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public ConditionalEdge(ConditionalGraphNode source, ConditionalGraphNode target, ConditionalEdgeType type, boolean isBackEdge ) {
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public ConditionalEdgeType getType() {
        return type;
    }

    public void setType(ConditionalEdgeType type) {
        this.type = type;
    }

    public ConditionalGraphNode getSource() {
        return this.source;
    }

    public ConditionalGraphNode getTarget() {
        return this.target;
    }

    public boolean isBackEdge() {
        return this.isBackEdge;
    }

    public void setBackEdge(boolean isLooopingEdge) {
        this.isBackEdge = isLooopingEdge;
    }
}
