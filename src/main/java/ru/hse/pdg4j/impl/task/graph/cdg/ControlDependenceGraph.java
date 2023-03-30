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
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalEdge;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraph;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraphNode;
import ru.hse.pdg4j.impl.task.graph.pdtg.PostDominatorInfo;
import org.jgrapht.graph.DefaultDirectedGraph;
import spoon.reflect.code.CtComment.CommentType;
import spoon.reflect.path.impl.CtRolePathElement;
import spoon.support.reflect.code.CtCommentImpl;

public class ControlDependenceGraph  {
    private ConditionalGraph info;
    private ConditionalGraph baseControlFlowGraph;
    private int counter = 0;
    private Integer nodeCounter = 0;
    private Set<ConditionalGraphNode> used;
    private Map<ConditionalGraphNode, Integer> in, out;
    private Map<ConditionalGraphNode, ConditionalGraphNode> parent;

    public ControlDependenceGraph(ConditionalGraph controlFlowGraph, ConditionalGraph info) {
        super();
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
    }

    public ConditionalGraph getControlDependenceGraph() {
        init();

        ConditionalGraph newGraph = new ConditionalGraph();
        dfs(info.getStart());
        for (var edge: baseControlFlowGraph.edgeSet()) {
            process(edge, newGraph);
        }
        return newGraph;
    }

    private void dfs(ConditionalGraphNode node) {
        used.add(node);
        in.put(node, ++counter);
        for (var edge: info.outgoingEdgesOf(node)) {
            if (!used.contains(edge.getTarget())) {
                dfs(edge.getTarget());
            }
        }
        out.put(node, ++counter);
    }

    private void process(ConditionalEdge edge, ConditionalGraph newGraph) {
        var source = edge.getSource();
        var target = edge.getTarget();
        if (in.get(target) == null || in.get(source) == null) {
            return;
        }
        if (in.get(target) < in.get(source) && out.get(source) < out.get(target)) {
            return;
        }
        if (source.getKind() != BranchKind.BRANCH) {
            return;
        }
        var tp = edge.getType();
        var tmp = source;
        while (target != null && !(in.get(target) < in.get(source) && out.get(source) < out.get(target))) {
            if (!newGraph.containsVertex(source)) {
                newGraph.addVertex(source);
            }
            if (!newGraph.containsVertex(target)) {
                newGraph.addVertex(target);
            }
            var newEdge = newGraph.addEdge(source, target);
            newEdge.setBackEdge(edge.isBackEdge());
            newEdge.setType(tp);

            var predEdge = this.info.incomingEdgesOf(target).stream().findFirst().orElse(null);
            if (predEdge == null) {
                target = null;
                continue;
            }
            target = predEdge.getSource();
        }

    }

}
