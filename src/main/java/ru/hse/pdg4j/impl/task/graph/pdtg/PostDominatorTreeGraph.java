package ru.hse.pdg4j.impl.task.graph.pdtg;

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
import org.jgrapht.graph.DefaultDirectedGraph;

public class PostDominatorTreeGraph extends ControlFlowGraph {

    private int counter = 0;
    public boolean isTreeCreated = false;
    private boolean isReversed;
    private Map<ControlFlowNode, ControlFlowNode> parent;
    private Map<ControlFlowNode, Integer> semi;
    private Map<Integer, ControlFlowNode> vertexes;
    private Map<ControlFlowNode, Set<ControlFlowNode>> bucket;
    private Map<ControlFlowNode, ControlFlowNode> dom;
    private Map<ControlFlowNode, ControlFlowNode> ancestor;
    private Map<ControlFlowNode, ControlFlowNode> label;
    public PostDominatorTreeGraph(boolean isReversed) {
        super(ControlFlowEdge.class);
        this.isReversed = isReversed;
        init();
    }

    public ControlFlowNode getStart() {
        return this.vertexSet().stream().min(new Comparator<ControlFlowNode>() {
            @Override
            public int compare(ControlFlowNode o1, ControlFlowNode o2) {
                return Integer.compare(inDegreeOf(o1), inDegreeOf(o2));
            }
        }).get();
    }

    public ControlFlowNode getEnd() {
        return this.vertexSet().stream().min(new Comparator<ControlFlowNode>() {
            @Override
            public int compare(ControlFlowNode o1, ControlFlowNode o2) {
                return Integer.compare(outDegreeOf(o1), outDegreeOf(o2));
            }
        }).get();
    }

    private void init () {
        this.counter = 0;
        this.semi = new LinkedHashMap<>();
        this.vertexes = new LinkedHashMap<>();
        this.bucket = new LinkedHashMap<>();
        this.parent = new LinkedHashMap<>();
        this.dom = new LinkedHashMap<>();
        this.ancestor = new LinkedHashMap<>();
        this.label = new LinkedHashMap<>();
        for (var v: this.vertexSet()) {
            this.semi.put(v, 0);
            this.ancestor.put(v, null);
            this.label.put(v, v);
        }
    }

    public void addToBucket(ControlFlowNode parent, ControlFlowNode child) {
        this.bucket.computeIfAbsent(parent, k -> new HashSet<>());
        this.bucket.get(parent).add(child);
    }

    public void deleteFromBucket(ControlFlowNode parent, ControlFlowNode child) {
        this.bucket.computeIfAbsent(parent, k -> new HashSet<>());
        this.bucket.get(parent).remove(child);
    }

    public ControlFlowEdge addEdge(ControlFlowNode source, ControlFlowNode target) {
        if (!this.containsVertex(source)) {
            this.addVertex(source);
        }

        if (!this.containsVertex(target)) {
            this.addVertex(target);
        }
        this.isTreeCreated = false;
        return (ControlFlowEdge) super.addEdge(source, target);
    }

    public List<ControlFlowNode> pred(ControlFlowNode node) {
        return this.incomingEdgesOf(node).stream().map(ControlFlowEdge::getSourceNode).collect(
            Collectors.toList());
    }

    private void dfs(ControlFlowNode node) {
        semi.put(node, ++counter);
        vertexes.put(counter, node);
        var edges = this.outgoingEdgesOf(node);
        for (var edge: edges) {
            var target = edge.getTargetNode();
            if (semi.get(target) == 0) {
                parent.put(target, node);
                dfs(target);
            }
        }
    }

    private ControlFlowNode EVAL(ControlFlowNode node) {
        if (this.ancestor.get(node) == null) {
            return node;
        }
        Process(node);
        return this.label.get(node);
    }

    private void Link(ControlFlowNode parent, ControlFlowNode node) {
        this.ancestor.put(node,parent);
    }

    private void Process(ControlFlowNode node) {
        if (this.ancestor.get(node) == null) {
            return;
        }
        if (this.ancestor.get(this.ancestor.get(node)) != null) {
            Process(this.ancestor.get(node));
            if (semi.get(label.get(this.ancestor.get(node))) < semi.get(label.get(node))) {
                label.put(node, label.get(this.ancestor.get(node)));
            }
            this.ancestor.put(node, this.ancestor.get(this.ancestor.get(node)));
        }
    }

    public PostDominatorInfo getPostDominatorInfo() {
        if (!this.isTreeCreated) {
            createPostDominatorTree();
        }
        ControlFlowGraph controlFlowGraph = new ControlFlowGraph();
        for (var v: this.vertexSet()) {
            if (this.dom.get(v) == null) {
                continue;
            }
            controlFlowGraph.addEdge(this.dom.get(v), v);
        }
        var info = new PostDominatorInfo(controlFlowGraph, this.getStart(), this.getEnd());
        return info;
    }

    public void createPostDominatorTree() {
        if (this.isTreeCreated) {
            return;
        }
        init();
        dfs(this.getStart());
        var n = this.vertexSet().size();

        var vers = this.vertexSet().stream().sorted(new Comparator<ControlFlowNode>() {
            @Override
            public int compare(ControlFlowNode o1, ControlFlowNode o2) {
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
