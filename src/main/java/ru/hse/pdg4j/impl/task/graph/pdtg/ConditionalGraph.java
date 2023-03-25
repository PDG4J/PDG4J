package ru.hse.pdg4j.impl.task.graph.pdtg;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowNode;
import fr.inria.controlflow.GraphVisPrettyPrinter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;

public class ConditionalGraph extends DefaultDirectedGraph<ConditionalGraphNode, ConditionalEdge> {
    private Map<ControlFlowNode, ConditionalGraphNode> mapToConditionalNode;
    private String name;

    public ConditionalGraph() {
        super(new EdgeFactory<ConditionalGraphNode, ConditionalEdge>() {
            @Override
            public ConditionalEdge createEdge(ConditionalGraphNode node, ConditionalGraphNode v1) {
                if (node.getKind() == BranchKind.BRANCH) {
                    return new ConditionalEdge(node, v1, ConditionalEdgeType.TRUE);
                }else {
                    return new ConditionalEdge(node, v1, ConditionalEdgeType.NONE);
                }
            }
        });
        mapToConditionalNode = new LinkedHashMap<>();
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

        if (!this.containsVertex(source)) {
            this.addVertex(source);
        }

        if (!this.containsVertex(target)) {
            this.addVertex(target);
        }

        var edge = (ConditionalEdge) super.addEdge(source, target);
        if (source.getKind() == BranchKind.BRANCH && super.outDegreeOf(source) > 1) {
            edge.setType(ConditionalEdgeType.FALSE);
        }
        edge.setBackEdge(isBack);
        return edge;
    }

    public ConditionalEdge addEdge(ConditionalGraphNode source, ConditionalGraphNode target) {
        if (!this.containsVertex(source)) {
            this.addVertex(source);
        }

        if (!this.containsVertex(target)) {
            this.addVertex(target);
        }

        var edge = (ConditionalEdge) super.addEdge(source, target);
        if (source.getKind() == BranchKind.BRANCH && super.outDegreeOf(source) > 1) {
            edge.setType(ConditionalEdgeType.FALSE);
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
