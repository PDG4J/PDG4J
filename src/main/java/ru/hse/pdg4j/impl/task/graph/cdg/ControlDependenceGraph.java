package ru.hse.pdg4j.impl.task.graph.cdg;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowEdge;
import fr.inria.controlflow.ControlFlowGraph;
import fr.inria.controlflow.ControlFlowNode;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import ru.hse.pdg4j.impl.task.graph.pdtg.PostDominatorInfo;
import org.jgrapht.graph.DefaultDirectedGraph;
import spoon.reflect.code.CtComment.CommentType;
import spoon.reflect.path.impl.CtRolePathElement;
import spoon.support.reflect.code.CtCommentImpl;

public class ControlDependenceGraph extends ControlFlowGraph {
    private PostDominatorInfo info;
    private ControlFlowGraph baseControlFlowGraph;
    private int counter = 0;
    private Integer nodeCounter = 0;
    private Set<ControlFlowNode> used;
    private Map<ControlFlowNode, Integer> in, out;
    private Map<ControlFlowNode, ControlFlowNode> parent;

    public ControlDependenceGraph(ControlFlowGraph controlFlowGraph, PostDominatorInfo info) {
        super(ControlFlowEdge.class);
        this.baseControlFlowGraph = controlFlowGraph;
        this.info = info;
        init();
    }

    private void init () {
        this.counter = 0;
        used = new HashSet<>();
        in = new LinkedHashMap<>();
        out = new LinkedHashMap<>();
        parent = new LinkedHashMap<>();

        dfs(info.getStart());
        for (var edge: baseControlFlowGraph.edgeSet()) {
            process(edge.getSourceNode(), edge.getTargetNode());
        }
    }

    private void dfs(ControlFlowNode node) {
        used.add(node);
        in.put(node, ++counter);
        for (var edge: info.getControlFlowGraph().outgoingEdgesOf(node)) {
            if (!used.contains(edge.getTargetNode())) {
                dfs(edge.getTargetNode());
            }
        }
        out.put(node, ++counter);
    }

    private void process(ControlFlowNode source, ControlFlowNode target) {
        if (in.get(target) == null || in.get(source) == null) {
            return;
        }
        if (in.get(target) < in.get(source) && out.get(source) < out.get(target)) {
            return;
        }
        var tmp = source;
        while (target != null && !(in.get(target) < in.get(source) && out.get(source) < out.get(target))) {
            if (!this.containsVertex(source)) {
                this.addVertex(source);
            }
            if (!this.containsVertex(target)) {
                this.addVertex(target);
            }
            if (parent.get(target) == null) {
                var comment = new CtCommentImpl();
                comment.setContent("R" + (++nodeCounter).toString());
                comment.setCommentType(CommentType.BLOCK);
                var parentNode = new ControlFlowNode(comment, this);
                this.addVertex(parentNode);
                this.addEdge(tmp, parentNode);
                this.addEdge(parentNode, target);
                parent.put(target, parentNode);
                tmp = parentNode;
            } else {
                this.addEdge(tmp, parent.get(target));
                break;
            }
            var edge = this.info.getControlFlowGraph().incomingEdgesOf(target).stream().findFirst().orElse(null);
            if (edge == null) {
                target = null;
                continue;
            }
            target = edge.getSourceNode();
        }
    }

}
