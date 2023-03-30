package ru.hse.pdg4j.impl.task.graph.cdg;

import fr.inria.controlflow.BranchKind;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.codehaus.plexus.util.CollectionUtils;
import org.jgrapht.alg.util.Pair;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalEdge;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalEdgeType;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraph;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraphNode;
import spoon.reflect.code.CtComment.CommentType;
import spoon.support.reflect.code.CtCommentImpl;


public class AddRegionalNodesGraph {
    private ConditionalGraph controlDependenceGraph;
    private Set<ConditionalGraphNode> used;
    private int counter = 0;


    public AddRegionalNodesGraph(ConditionalGraph controlDependenceGraph) {
        super();
        this.controlDependenceGraph = new ConditionalGraph();
        for (var edge: controlDependenceGraph.edgeSet()) {
            this.controlDependenceGraph.addEdge(edge.getSource(), edge.getTarget(), edge.isBackEdge());
        }
        this.controlDependenceGraph = controlDependenceGraph;
        init();
    }

    private void init () {
        this.counter = 0;
        this.used = new HashSet<>();
    }

    public void dfs(ConditionalGraphNode node, ConditionalGraph graph) {
        this.used.add(node);
        for (var newNode : this.controlDependenceGraph.outgoingEdgesOf(node)) {
            if (!this.used.contains(newNode.getTarget())) {
                dfs(newNode.getTarget(), graph);
            }
        }

        var edges = this.controlDependenceGraph.outgoingEdgesOf(node).stream().collect(Collectors.groupingBy(ConditionalEdge::getType, Collectors.toList()));

        for (var entry: edges.entrySet()) {
            var tp = entry.getKey();
            var nodes = entry.getValue();

            var comment = new CtCommentImpl();
            comment.setContent("R" + (++this.counter));
            comment.setCommentType(CommentType.BLOCK);

            var tpNode = new ConditionalGraphNode(comment, graph, BranchKind.STATEMENT);
            graph.addVertex(tpNode);
            graph.addEdge(node, tpNode, tp);

            for (var oldNode: nodes) {
                var target = oldNode.getTarget();
                if (graph.containsEdge(node, target)) {
                    graph.removeEdge(node, target);
                }
                graph.addEdge(tpNode, target, ConditionalEdgeType.NONE);
            }
        }
    }

    public ConditionalGraph getControlDependenceGraph() {
        init();
        ConditionalGraph newGraph = new ConditionalGraph();
        for (var edge: this.controlDependenceGraph.edgeSet()) {
            newGraph.addEdge(edge);
        }
        dfs(this.controlDependenceGraph.getStart(), newGraph);
        return newGraph;
    }

}
