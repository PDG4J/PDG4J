package ru.hse.pdg4j.impl.task.graph.pdtg;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowEdge;
import fr.inria.controlflow.ControlFlowGraph;
import fr.inria.controlflow.ControlFlowNode;
import java.util.HashMap;
import java.util.Iterator;


public class GraphVisPrettyPrinterForConditionalGraph {
    private final ConditionalGraph graph;

    public GraphVisPrettyPrinterForConditionalGraph(ConditionalGraph graph) {
        this.graph = graph;
    }

    public String print() {
        StringBuilder sb = (new StringBuilder("digraph ")).append(this.graph.getName()).append(" { \n");
        sb.append("node [fontsize = 8];\n");
        int i = 0;
        HashMap<ConditionalGraphNode, Integer> nodeIds = new HashMap();
        Iterator var4 = this.graph.vertexSet().iterator();

        while(var4.hasNext()) {
            ConditionalGraphNode n = (ConditionalGraphNode)var4.next();
            ++i;
            this.printNode(i, n, sb);
            nodeIds.put(n, i);
        }

        var4 = this.graph.edgeSet().iterator();

        while(var4.hasNext()) {
            ConditionalEdge e = (ConditionalEdge)var4.next();
            if (e.isBackEdge()) {
                sb.append(nodeIds.get(e.getSource())).append(" -> ").append(nodeIds.get(e.getTarget())).append("[style=dashed];\n ");
            } else {
                sb.append(nodeIds.get(e.getSource())).append(" -> ").append(nodeIds.get(e.getTarget())).append("[label=\"" + e.getType() + "\"]" ).append(" ;\n ");
            }
        }

        sb.append("\n }");
        return sb.toString();
    }

    private String printNode(int i, ConditionalGraphNode n, StringBuilder sb) {
        String labelStr = " [shape=rectangle, label=\"";
        if (n.getKind() == BranchKind.BRANCH) {
            labelStr = " [shape=diamond, label=\"";
        } else if (n.getKind() == BranchKind.BEGIN) {
            labelStr = " [shape=Mdiamond, label=\"";
        } else if (n.getKind() != BranchKind.BLOCK_BEGIN && n.getKind() != BranchKind.BLOCK_END) {
            if (n.getKind() == BranchKind.EXIT) {
                labelStr = " [shape=doublecircle, label=\"";
            } else if (n.getKind() == BranchKind.CONVERGE) {
                labelStr = " [shape=point label=\"";
            }
        } else {
            labelStr = " [shape=rectangle, style=filled, fillcolor=gray, label=\"";
        }

        sb.append(i).append(labelStr).append(n.toString().replace("\"", "quot ")).append(" \"]").append(";\n");
        return sb.toString();
    }
}