package ru.hse.pdg4j.impl.task.graph.pdtg;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowGraph;
import fr.inria.controlflow.ControlFlowNode;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.*;

public class ConditionalGraph extends DefaultDirectedGraph<ConditionalGraphNode, ConditionalEdge> {
    private final Map<ControlFlowNode, ConditionalGraphNode> mapToConditionalNode;
    private String name;

    public ConditionalGraph() {
        super(new EdgeFactory<ConditionalGraphNode, ConditionalEdge>() {
            @Override
            public ConditionalEdge createEdge(ConditionalGraphNode node, ConditionalGraphNode v1) {
                return new ConditionalEdge(node, v1, ConditionalEdgeType.NONE);
            }
        });
        mapToConditionalNode = new LinkedHashMap<>();
    }

    public void build(ControlFlowGraph graph) {
        Set<ControlFlowNode> set = new HashSet<>();
        var start = graph.findNodesOfKind(BranchKind.BEGIN).get(0);
        dfs(graph, start, set);
    }

    public void dfs(ControlFlowGraph graph, ControlFlowNode node, Set<ControlFlowNode> set) {
        set.add(node);
        for (var edge : graph.outgoingEdgesOf(node)) {
            this.addEdge(node, edge.getTargetNode(), edge.isBackEdge());
            if (!set.contains(edge.getTargetNode())) {
                dfs(graph, edge.getTargetNode(), set);
            }
        }
    }

    public ConditionalEdge addEdge(ControlFlowNode sourceNode, ControlFlowNode targetNode, boolean isBack) {
        var source = mapToConditionalNode.get(sourceNode);
        var target = mapToConditionalNode.get(targetNode);
        if (source == null) {
            source = new ConditionalGraphNode(sourceNode.getStatement(), this, sourceNode.getKind());
            mapToConditionalNode.put(sourceNode, source);
        }
        if (target == null) {
            target = new ConditionalGraphNode(targetNode.getStatement(), this, targetNode.getKind());
            mapToConditionalNode.put(targetNode, target);
        }

        return addEdge(source, target, isBack);
    }

    public ConditionalEdge addEdge(ConditionalGraphNode source, ConditionalGraphNode target, ConditionalEdgeType kind, boolean isBack) {
        var edge = addEdge(source, target, isBack);
        edge.setType(kind);
        return edge;
    }

    public ConditionalEdge addEdge(ConditionalGraphNode source, ConditionalGraphNode target, ConditionalEdgeType kind) {
        var edge = addEdgeBasic(source, target);
        edge.setType(kind);
        return edge;
    }

    public ConditionalEdge addEdgeBasic(ConditionalGraphNode source, ConditionalGraphNode target) {

        if (!this.containsVertex(source)) {
            this.addVertex(source);
        }

        if (!this.containsVertex(target)) {
            this.addVertex(target);
        }

        if (this.containsEdge(source, target)) {
            return this.getEdge(source, target);
        }

        var edge = (ConditionalEdge) super.addEdge(source, target);
        if (source.getKind() == BranchKind.BRANCH && target.getKind() != BranchKind.STATEMENT) {
            if (super.outDegreeOf(source) == 1) {
                edge.setType(ConditionalEdgeType.TRUE);
            } else {
                edge.setType(ConditionalEdgeType.FALSE);
            }
        }
        return edge;
    }

    public ConditionalEdge addEdge(ConditionalGraphNode source, ConditionalGraphNode target, boolean isBack) {
        var edge = addEdgeBasic(source, target);
        edge.setBackEdge(isBack);
        return edge;
    }

    public ConditionalEdge addEdge(ConditionalEdge edge) {
        var source = edge.getSource();
        var target = edge.getTarget();

        if (!this.containsVertex(source)) {
            this.addVertex(source);
        }

        if (!this.containsVertex(target)) {
            this.addVertex(target);
        }

        var newEdge = this.addEdge(source, target);
        newEdge.setBackEdge(edge.isBackEdge());
        newEdge.setType(edge.getType());
        return newEdge;
    }

    public ConditionalEdge addEdge(ConditionalGraphNode source, ConditionalGraphNode target) {
        if (!this.containsVertex(source)) {
            this.addVertex(source);
        }

        if (!this.containsVertex(target)) {
            this.addVertex(target);
        }

        var edge = (ConditionalEdge) super.addEdge(source, target);
        if (source.getKind() == BranchKind.BRANCH && target.getKind() != BranchKind.STATEMENT) {
            if (super.outDegreeOf(source) == 1) {
                edge.setType(ConditionalEdgeType.TRUE);
            } else {
                edge.setType(ConditionalEdgeType.FALSE);
            }
        }
        return edge;
    }

    public ConditionalGraphNode getStart() {
        if (vertexSet().size() == 0) {
            return null;
        }
        return this.vertexSet().stream().min(new Comparator<ConditionalGraphNode>() {
            @Override
            public int compare(ConditionalGraphNode o1, ConditionalGraphNode o2) {
                return Integer.compare(inDegreeOf(o1), inDegreeOf(o2));
            }
        }).get();
    }

    public ConditionalGraphNode getEnd() {
        if (vertexSet().size() == 0) {
            return null;
        }
        return this.vertexSet().stream().min(new Comparator<ConditionalGraphNode>() {
            @Override
            public int compare(ConditionalGraphNode o1, ConditionalGraphNode o2) {
                return Integer.compare(outDegreeOf(o1), outDegreeOf(o2));
            }
        }).get();
    }

    public String toGraphVisText() {
        GraphVisPrettyPrinterForConditionalGraph p = new GraphVisPrettyPrinterForConditionalGraph(this);
        return p.print();
    }

    public String getName() {
        return this.name;
    }
}
