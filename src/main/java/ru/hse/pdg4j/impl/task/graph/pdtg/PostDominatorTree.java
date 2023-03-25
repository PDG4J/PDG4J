package ru.hse.pdg4j.impl.task.graph.pdtg;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowGraph;
import fr.inria.controlflow.ControlFlowNode;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.EdgeFactory;

public class PostDominatorTree {

    private int counter = 0;
    public boolean isTreeCreated = false;
    private ConditionalGraph graph;
    private Map<ConditionalGraphNode, ConditionalGraphNode> parent;
    private Map<ConditionalGraphNode, Integer> semi;
    private Map<Integer, ConditionalGraphNode> vertexes;
    private Map<ConditionalGraphNode, Set<ConditionalGraphNode>> bucket;
    private Map<ConditionalGraphNode, ConditionalGraphNode> dom;
    private Map<ConditionalGraphNode, ConditionalGraphNode> ancestor;
    private Map<ConditionalGraphNode, ConditionalGraphNode> label;

    public PostDominatorTree() {
        graph = new ConditionalGraph();
        init();
    }

    private void init () {
        counter = 0;
        semi = new LinkedHashMap<>();
        vertexes = new LinkedHashMap<>();
        bucket = new LinkedHashMap<>();
        parent = new LinkedHashMap<>();
        dom = new LinkedHashMap<>();
        ancestor = new LinkedHashMap<>();
        label = new LinkedHashMap<>();
        for (var v: graph.vertexSet()) {
            semi.put(v, 0);
            ancestor.put(v, null);
            label.put(v, v);
        }
    }

    public void addToBucket(ConditionalGraphNode parent, ConditionalGraphNode child) {
        bucket.computeIfAbsent(parent, k -> new HashSet<>());
        bucket.get(parent).add(child);
    }

    public List<ConditionalGraphNode> pred(ConditionalGraphNode node) {
        return this.graph.incomingEdgesOf(node).stream().map(ConditionalEdge::getSource).collect(
            Collectors.toList());
    }

    private void dfs(ConditionalGraphNode node) {
        semi.put(node, ++counter);
        vertexes.put(counter, node);
        var edges = graph.outgoingEdgesOf(node);
        for (var edge: edges) {
            var target = edge.getTarget();
            if (semi.get(target) == 0) {
                parent.put(target, node);
                dfs(target);
            }
        }
    }

    private ConditionalGraphNode EVAL(ConditionalGraphNode node) {
        if (ancestor.get(node) == null) {
            return node;
        }
        Process(node);
        return label.get(node);
    }

    private void Link(ConditionalGraphNode parent, ConditionalGraphNode node) {
        ancestor.put(node,parent);
    }

    private void Process(ConditionalGraphNode node) {
        if (ancestor.get(node) == null) {
            return;
        }
        if (ancestor.get(ancestor.get(node)) != null) {
            Process(ancestor.get(node));
            if (semi.get(label.get(ancestor.get(node))) < semi.get(label.get(node))) {
                label.put(node, label.get(ancestor.get(node)));
            }
            ancestor.put(node, ancestor.get(ancestor.get(node)));
        }
    }

    public ConditionalGraph getPostDominatorInfo() {
        if (!isTreeCreated) {
            createPostDominatorTree();
        }
        ConditionalGraph newGraph = new ConditionalGraph();
        for (var v: graph.vertexSet()) {
            if (dom.get(v) == null) {
                continue;
            }
            newGraph.addEdge(dom.get(v), v);
        }
        return newGraph;
    }

    public void addEdge(ConditionalGraphNode source, ConditionalGraphNode target, ConditionalEdgeType kind, boolean isBacking) {
        isTreeCreated = false;
        var edge = graph.addEdge(source, target);
        edge.setType(kind);
        edge.setBackEdge(isBacking);
    }

    public void createPostDominatorTree() {
        if (this.isTreeCreated) {
            return;
        }
        init();
        dfs(graph.getStart());
        var n = graph.vertexSet().size();

        var vers = graph.vertexSet().stream().sorted(new Comparator<ConditionalGraphNode>() {
            @Override
            public int compare(ConditionalGraphNode o1, ConditionalGraphNode o2) {
                return -1 * Integer.compare(semi.get(o1), semi.get(o2));
            }
        }).limit(n - 1).collect(Collectors.toList());

        for (var w: vers) {
            for (var v: pred(w)) {
                var node = EVAL(v);
                if (semi.get(node) < semi.get(w)) {
                    semi.put(w, semi.get(node));
                }
            }
            addToBucket(vertexes.get(semi.get(w)), w);
            Link(parent.get(w), w);
            for (var v: bucket.getOrDefault(parent.get(w), new HashSet<>())) {
                var node = EVAL(v);
                if (semi.get(node) < semi.get(v)) {
                    dom.put(v, node);
                } else {
                    dom.put(v, vertexes.get(semi.get(parent.get(w))));
                }
            }
            bucket.remove(parent.get(w));
        }
        for (int i = 2; i <= counter; ++i) {
            var w = vertexes.get(i);
            if (dom.get(w) != vertexes.get(semi.get(w))) {
                dom.put(w, dom.get(dom.get(w)));
            }
        }
        this.isTreeCreated = true;
    }
}
