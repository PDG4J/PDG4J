package ru.hse.pdg4j.impl.task.graph.dfg;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowGraph;
import fr.inria.controlflow.ControlFlowNode;
import fr.inria.dataflow.InitializedVariables;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import org.codehaus.plexus.util.CollectionUtils;
import org.jgrapht.alg.util.Pair;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalEdge;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalEdgeType;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraph;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraphNode;
import spoon.reflect.code.CtComment;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtVariableReference;
import spoon.support.reflect.code.CtAssignmentImpl;
import spoon.support.reflect.code.CtCommentImpl;
import spoon.support.reflect.code.CtContinueImpl;
import spoon.support.reflect.reference.CtVariableReferenceImpl;

public class DataFlowGraph {

    private ConditionalGraph baseConditionalGraph;
    private AtomicInteger counter = new AtomicInteger(0);
    private Map<ConditionalGraphNode, Integer> nodes;
    private Set<ConditionalGraphNode> loopNodes;

    private Map<ConditionalGraphNode, ConditionalGraphNode> tmpNodesMap;

    private Map<ConditionalGraphNode, Map<String, List<ConditionalGraphNode>>> DatadependenceStartSet;

    private Map<ConditionalGraphNode, Map<String, List<ConditionalGraphNode>>> DatadependenceReturnSet;

    private Set<ConditionalGraphNode> tmpNodes;


    private Set<ConditionalGraphNode> used;

    public DataFlowGraph(ConditionalGraph baseConditionalGraph) {
        super();
        this.baseConditionalGraph = baseConditionalGraph;
        init();
    }

    private void init() {
        nodes = new HashMap<>();
        used = new HashSet<>();
        tmpNodesMap = new HashMap<>();
        tmpNodes = new HashSet<>();
        loopNodes = new HashSet<>();
        DatadependenceReturnSet = new HashMap<>();
        DatadependenceStartSet = new HashMap<>();
    }

    private boolean isIfhasallContinue(ConditionalGraphNode node, ConditionalGraph newGraph) {
        boolean check = true;
        for (var edge : newGraph.incomingEdgesOf(node)) {
            if (!(edge.getSource().getStatement() != null && edge.getSource()
                .getStatement() instanceof CtContinueImpl)) {
                check = false;
            }
        }
        return check;
    }

    private boolean isExported(CtVariableReference variableReference) {
        return (variableReference.prettyprint().contains("."));
    }

    private List<String> findNonLocalVariables(Map<String, List<ConditionalGraphNode>> set1,
        Map<String, List<ConditionalGraphNode>> set2,
        List<Pair<ConditionalGraphNode, ConditionalGraphNode>> pairs) {
        List<String> ans = new ArrayList<>();
        for (var entry : set2.entrySet()) {
            var variable = entry.getKey();
            if (set1.containsKey(variable)) {
                for (var entry1 : set1.get(variable)) {
                    entry.getValue().forEach((ConditionalGraphNode f) -> {
                        pairs.add(new Pair<>(entry1, f));
                    });
                }
                ans.add(variable);
            }
        }
        return ans;
    }

    private void findBlocks(ConditionalGraphNode node, Stack<ConditionalGraphNode> stack,
        ConditionalGraph newGraph, ConditionalGraphNode parent,
        Map<String, List<ConditionalGraphNode>> start,
        Map<String, List<ConditionalGraphNode>> end,
        List<Pair<ConditionalGraphNode, ConditionalGraphNode>> newPairs) {

        if (node.getKind() == BranchKind.BRANCH) {
            stack.add(node);
            DatadependenceStartSet.put(node, start);
            DatadependenceReturnSet.put(node, end);
        } else if (node.getKind() == BranchKind.STATEMENT) {
            var controlFlowGraph = new ControlFlowGraph();
            controlFlowGraph.getEdgeFactory();
            var beginNode = new ControlFlowNode(newCommend(node.getStatement().getFactory()),
                controlFlowGraph, BranchKind.BEGIN);
            var newNode = new ControlFlowNode(node.getStatement(), controlFlowGraph,
                BranchKind.STATEMENT);
            var endNode = new ControlFlowNode(newCommend(node.getStatement().getFactory()),
                controlFlowGraph, BranchKind.EXIT);
            controlFlowGraph.addEdge(beginNode, newNode);
            controlFlowGraph.addEdge(newNode, endNode);

            InitializedVariables newVariables = new InitializedVariables();
            newVariables.setIncludeDefinedInNode(false);

            newVariables.run(controlFlowGraph.getExitNode());

            var used = newVariables.getUsed();
            var defined = CollectionUtils.subtract(newVariables.getDefined(), used);

            System.out.println(node + " " + defined + " " + used);

            for (var variable : used) {
                if (isExported(variable)) {
                    continue;
                }
                var variableName = variable.getSimpleName();
                if (end.containsKey(variableName)) {
                    for (var oldNode : end.get(variableName)) {
                        newPairs.add(new Pair<>(oldNode, node));
                    }
                } else {
                    if (!start.containsKey(variableName)) {
                        start.put(variableName, new ArrayList<>());
                    }
                    var lst = start.get(variableName);
                    lst.add(node);
                    start.put(variableName, lst);
                }
            }

            for (var variable : defined) {
                if (isExported(variable)) {
                    continue;
                }
                var variableName = variable.getSimpleName();
                if (end.containsKey(variableName)) {
                    for (var oldNode : end.get(variableName)) {
                        newPairs.add(new Pair<>(oldNode, node));
                    }
                } else {
                    var lst =  new ArrayList<ConditionalGraphNode>();
                    lst.add(node);
                    start.put(variableName, lst);
                }

                var lst = new ArrayList<ConditionalGraphNode>();
                lst.add(node);
                end.put(variableName, lst);
            }
        }

        if (!loopNodes.contains(parent) && node.getKind() == BranchKind.CONVERGE) {
            var count = nodes.get(node);
            count--;
            nodes.put(node, count);

            if (!DatadependenceStartSet.containsKey(node)) {
                DatadependenceStartSet.put(node, new HashMap<>());
                DatadependenceReturnSet.put(node, new HashMap<>());
            }

            var set = DatadependenceStartSet.get(node);
            for (var entry : start.entrySet()) {
                var variable = entry.getKey();
                if (!set.containsKey(variable)) {
                    set.put(variable, new ArrayList<>());
                }
                var list = set.get(variable);
                list.addAll(entry.getValue());
                set.put(variable, list);
            }
            DatadependenceStartSet.put(node, set);

            set = DatadependenceReturnSet.get(node);
            for (var entry : end.entrySet()) {
                var variable = entry.getKey();
                if (!set.containsKey(variable)) {
                    set.put(variable, new ArrayList<>());
                }
                var list = set.get(variable);
                list.addAll(entry.getValue());
                set.put(variable, list);
            }
            DatadependenceReturnSet.put(node, set);

            if (count != 0) {
                return;
            }

            if (tmpNodes.contains(node)) {
                if (isIfhasallContinue(node, newGraph)) {
                    stack.pop();
                }
                var dataDependencyParent = stack.pop();
                var tmpDataDependencyEndSet = DatadependenceReturnSet.get(dataDependencyParent);
                var localVariablesStarted = DatadependenceStartSet.get(node);
                var localVariablesEnded = DatadependenceReturnSet.get(node);
                var overridedVariables = findNonLocalVariables(tmpDataDependencyEndSet, localVariablesStarted,
                    newPairs);

                for (var variable : overridedVariables) {
                    if (!localVariablesEnded.containsKey(variable)) {
                        continue;
                    }
                    for (var entry1 : tmpDataDependencyEndSet.get(variable)) {
                        localVariablesStarted.get(variable).forEach(
                            (ConditionalGraphNode nn) -> newPairs.add(new Pair<>(entry1, nn)));
                    }
                        var lst = tmpDataDependencyEndSet.get(variable);
                        lst.addAll(localVariablesEnded.get(variable));
                        tmpDataDependencyEndSet.put(variable, lst);
                }
                DatadependenceReturnSet.put(dataDependencyParent, tmpDataDependencyEndSet);

            } else {
                var dataDependencyParent = stack.pop();
                var tmpDataDependencyStartSet = DatadependenceStartSet.get(dataDependencyParent);
                var tmpDataDependencyEndSet = DatadependenceReturnSet.get(dataDependencyParent);
                var variables = DatadependenceStartSet.get(node);

                var overridedVariables = findNonLocalVariables(tmpDataDependencyEndSet, variables,
                    newPairs);

                start = tmpDataDependencyStartSet;
                end = tmpDataDependencyEndSet;
                for (var entry : DatadependenceReturnSet.get(node).entrySet()) {
                    if (overridedVariables.contains(entry.getKey())) {
                        end.put(entry.getKey(), entry.getValue());
                    }
                }
            }


        }

        used.add(node);
        for (var edge : newGraph.outgoingEdgesOf(node)) {
            var target = edge.getTarget();
            if (!used.contains(target)) {
                if (loopNodes.contains(node)) {
                    if (target.getKind() == BranchKind.CONVERGE) {
                        findBlocks(target, stack, newGraph, node, DatadependenceStartSet.get(node),
                            DatadependenceReturnSet.get(node), newPairs);
                    } else {
                        findBlocks(target, stack, newGraph, node, new HashMap<>(), new HashMap<>(),
                            newPairs);
                    }
                } else if (node.getKind() == BranchKind.BRANCH) {
                    if (target.getKind() == BranchKind.CONVERGE) {
                        var newEnd = new HashMap<String, List<ConditionalGraphNode>>();
                        for (var variable: end.entrySet()) {
                            var lst = new ArrayList<>(variable.getValue());
                            newEnd.put(variable.getKey(), lst);
                        }
                        findBlocks(target, stack, newGraph, node, new HashMap<>(), newEnd, newPairs);
                    } else {
                        findBlocks(target, stack, newGraph, node, new HashMap<>(), new HashMap<>(),
                            newPairs);
                    }
                } else {
                    findBlocks(target, stack, newGraph, node, start, end, newPairs);
                }
            }
        }

    }

    private void basicDfs(ConditionalGraphNode node, ConditionalGraph newGraph) {
        used.add(node);
        var edges = this.baseConditionalGraph.outgoingEdgesOf(node);
        for (var edge : edges) {
            var target = edge.getTarget();

            if (edge.isBackEdge()) {
                loopNodes.add(target);

                if (!tmpNodesMap.containsKey(target)) {
                    var comment = newCommend(node.getStatement().getFactory());
                    var newNode = new ConditionalGraphNode(comment, newGraph, BranchKind.CONVERGE);
                    newGraph.addVertex(newNode);
                    newGraph.addEdge(newNode, target, ConditionalEdgeType.NONE, true);

                    tmpNodes.add(newNode);
                    tmpNodesMap.put(target, newNode);
                    nodes.put(newNode, 0);
                }
                var tmpNode = tmpNodesMap.get(target);
                nodes.put(tmpNode, nodes.get(tmpNode) + 1);
                newGraph.addEdge(node, tmpNode, ConditionalEdgeType.NONE, false);
                newGraph.removeEdge(node, target);
            }

            if (!used.contains(target)) {
                basicDfs(target, newGraph);
            } else if (target.getKind() == BranchKind.CONVERGE) {
                nodes.put(target, nodes.get(target) + 1);
            }
        }
        if (node.getKind() == BranchKind.CONVERGE) {
            nodes.put(node, 1);
        }
    }

    public ConditionalGraph getControlDependenceGraph() {
        init();

        ConditionalGraph newGraph = new ConditionalGraph();
        for (var edge : this.baseConditionalGraph.edgeSet()) {
            newGraph.addEdge(edge);
        }

        basicDfs(baseConditionalGraph.getStart(), newGraph);

        used = new HashSet<>();
        var stack = new Stack<ConditionalGraphNode>();
        var newEdges = new ArrayList<Pair<ConditionalGraphNode, ConditionalGraphNode>>();
        findBlocks(newGraph.getStart(), stack, newGraph, null,
            new HashMap<>(), new HashMap<>(), newEdges);
        newEdges.forEach(
            (Pair<ConditionalGraphNode, ConditionalGraphNode> pair) -> newGraph.addEdge(pair.first,
                pair.second,
                ConditionalEdgeType.DATADEPEDENCE));
        return newGraph;
    }

    private CtCommentImpl newCommend(Factory factory) {
        var comment = new CtCommentImpl();
        comment.setContent("R" + (counter.getAndIncrement()));
        comment.setCommentType(CtComment.CommentType.BLOCK);
        comment.setFactory(factory);
        return comment;
    }

}
