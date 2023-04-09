package ru.hse.pdg4j.impl.task.graph.dfg;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowGraph;
import fr.inria.controlflow.ControlFlowNode;
import fr.inria.dataflow.InitializedVariables;
import java.util.ArrayList;
import java.util.Collection;
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
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtVariableReference;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.reflect.code.CtAssignmentImpl;
import spoon.support.reflect.code.CtCommentImpl;
import spoon.support.reflect.code.CtContinueImpl;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtLocalVariableImpl;
import spoon.support.reflect.code.CtOperatorAssignmentImpl;
import spoon.support.reflect.reference.CtVariableReferenceImpl;

public class DataFlowGraph {

    private ConditionalGraph baseConditionalGraph;
    private AtomicInteger counter = new AtomicInteger(0);
    private Map<ConditionalGraphNode, Integer> nodes;
    private CtMethod<?> methodInfo;
    private Set<ConditionalGraphNode> loopNodes;

    private Map<ConditionalGraphNode, ConditionalGraphNode> tmpNodesMap;

    private Map<ConditionalGraphNode, Map<String, List<ConditionalGraphNode>>> DatadependenceLinks;

    private Set<ConditionalGraphNode> tmpNodes;

    private Map<String, ConditionalGraphNode> extraNodes;


    private Set<ConditionalGraphNode> used;

    public DataFlowGraph(ConditionalGraph baseConditionalGraph, CtMethod<?> method) {
        super();
        this.baseConditionalGraph = baseConditionalGraph;
        this.methodInfo = method;
        init();
    }

    private void init() {
        nodes = new HashMap<>();
        used = new HashSet<>();
        tmpNodesMap = new HashMap<>();
        tmpNodes = new HashSet<>();
        loopNodes = new HashSet<>();
        DatadependenceLinks = new HashMap<>();
        extraNodes = new HashMap<>();
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

    private Map<String, List<ConditionalGraphNode>> deleteLocalVariables (Map<String, List<ConditionalGraphNode>> a, Map<String, List<ConditionalGraphNode>> b) {
        Map<String, List<ConditionalGraphNode>> ans = new HashMap<>();
        for (var variable: b.entrySet()) {
            var variableName = variable.getKey();
            if (a.containsKey(variableName) || extraNodes.containsKey(variableName)) {
                var lst = new ArrayList<>(variable.getValue());
                ans.put(variableName, lst);
            }
        }
        return ans;
    }

    private Pair<Collection<CtVariableReference>, Collection<CtVariableReference>> getVariables (CtElement statement, boolean isStatement) {
        var controlFlowGraph = new ControlFlowGraph();
        var beginNode = new ControlFlowNode(newCommend(), controlFlowGraph, BranchKind.BEGIN);

        if (statement.getFactory() == null) {
            statement.setFactory(
                new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment()));
        }
        ControlFlowNode newNode;
        if (isStatement) {
            newNode = new ControlFlowNode(statement, controlFlowGraph,
                BranchKind.STATEMENT);
        } else {
            newNode = new ControlFlowNode(statement, controlFlowGraph,
                BranchKind.BRANCH);
        }

        var endNode = new ControlFlowNode(newCommend(),
            controlFlowGraph, BranchKind.EXIT);

        controlFlowGraph.addEdge(beginNode, newNode);
        controlFlowGraph.addEdge(newNode, endNode);

        InitializedVariables newVariables = new InitializedVariables();
        newVariables.setIncludeDefinedInNode(false);

        newVariables.run(controlFlowGraph.getExitNode());

        var used = newVariables.getUsed();
        var defined = CollectionUtils.subtract(newVariables.getDefined(), used);
        return new Pair<>(defined, used);
    }

    private void processBlocks(ConditionalGraphNode node, ConditionalGraphNode parent, Map<String, List<ConditionalGraphNode>> variablesLinks, ConditionalGraph newGraph, Stack<ConditionalGraphNode> stack, List<Pair<ConditionalGraphNode, ConditionalGraphNode>> newPairs) {
        if (node.getKind() == BranchKind.BRANCH) {
            stack.add(node);
            if (!(node.getStatement() instanceof CtCommentImpl)) {
                var variables = getVariables(node.getStatement(), false);
                var used = variables.second;

                for (var variable : used) {
                    var variableName = variable.prettyprint();
                    if (variablesLinks.containsKey(variableName)) {
                        for (var oldVar : variablesLinks.get(variableName)) {
                            newPairs.add(new Pair<>(oldVar, node));
                        }
                    } else if (extraNodes.containsKey(variableName)) {
                        var tmpNode = extraNodes.get(variableName);
                        newPairs.add(new Pair<>(tmpNode, node));
                        var lst = new ArrayList<ConditionalGraphNode>();
                        lst.add(tmpNode);
                        variablesLinks.put(variableName, lst);
                    } else if (variableName.contains(".")) {
                        var tmpNode = new ConditionalGraphNode(variable, newGraph);
                        extraNodes.put(variableName, tmpNode);
                        newPairs.add(new Pair<>(tmpNode, node));
                        var lst = new ArrayList<ConditionalGraphNode>();
                        lst.add(tmpNode);
                        variablesLinks.put(variableName, lst);
                    }
                }
            }
        } else if (node.getKind() == BranchKind.STATEMENT) {
            var variables = getVariables(node.getStatement(), true);
            var defined = variables.first;
            var used = variables.second;

            for (var variable : used) {
                var variableName = variable.prettyprint();
                if (variablesLinks.containsKey(variableName)) {
                    for (var oldVar: variablesLinks.get(variableName)) {
                        newPairs.add(new Pair<>(oldVar, node));
                    }
                } else if (extraNodes.containsKey(variableName)) {
                    var tmpNode = extraNodes.get(variableName);
                    newPairs.add(new Pair<>(tmpNode, node));
                    var lst = new ArrayList<ConditionalGraphNode>();
                    lst.add(tmpNode);
                    variablesLinks.put(variableName, lst);
                } else if (variableName.contains(".")) {
                    var tmpNode = new ConditionalGraphNode(variable, newGraph);
                    extraNodes.put(variableName, tmpNode);
                    newPairs.add(new Pair<>(tmpNode, node));
                    var lst = new ArrayList<ConditionalGraphNode>();
                    lst.add(tmpNode);
                    variablesLinks.put(variableName, lst);
                }
            }

            for (var variable : defined) {
                var variableName = variable.prettyprint();
                if (variablesLinks.containsKey(variableName)) {
                    for (var oldVar: variablesLinks.get(variableName)) {
                        newPairs.add(new Pair<>(oldVar, node));
                    }
                } else if (extraNodes.containsKey(variableName)) {
                    var tmpNode = extraNodes.get(variableName);
                    newPairs.add(new Pair<>(tmpNode, node));
                } else if (variableName.contains(".")) {
                    var tmpNode = new ConditionalGraphNode(variable, newGraph);
                    extraNodes.put(variableName, tmpNode);
                    newPairs.add(new Pair<>(tmpNode, node));
                }
                var lst = new ArrayList<ConditionalGraphNode>();
                lst.add(node);
                variablesLinks.put(variableName, lst);
            }
        }

        if (!loopNodes.contains(parent) && node.getKind() == BranchKind.CONVERGE) {
            var count = nodes.get(node);
            count--;
            nodes.put(node, count);

            if (!DatadependenceLinks.containsKey(node)) {
                DatadependenceLinks.put(node, new HashMap<>());
            }

            var tmpDatadependenceLinks = DatadependenceLinks.get(node);

            for (var variable: variablesLinks.entrySet()) {
                var variableName = variable.getKey();
                if (!tmpDatadependenceLinks.containsKey(variableName)) {
                    tmpDatadependenceLinks.put(variableName, new ArrayList<>());
                }
                var oldLst = tmpDatadependenceLinks.get(variableName);
                var lst = variable.getValue();
                var intersect = CollectionUtils.intersection(oldLst, lst);
                oldLst.addAll(CollectionUtils.subtract(lst, intersect));
                tmpDatadependenceLinks.put(variableName, oldLst);
            }

            DatadependenceLinks.put(node, tmpDatadependenceLinks);

            if (count != 0) {
                return;
            }


            if (tmpNodes.contains(node)) {
                if (isIfhasallContinue(node, newGraph)) {
                    var oldNode = stack.pop();
                    tmpDatadependenceLinks = deleteLocalVariables(DatadependenceLinks.get(oldNode), tmpDatadependenceLinks);
                }
            }

            var dataDependencyGraphParent = stack.pop();
            tmpDatadependenceLinks = deleteLocalVariables(DatadependenceLinks.get(dataDependencyGraphParent), tmpDatadependenceLinks);

            if (loopNodes.contains(dataDependencyGraphParent)) {
                var oldParentDataDependencyGraph = DatadependenceLinks.get(dataDependencyGraphParent);
                for (var variable: tmpDatadependenceLinks.entrySet()) {
                    var variableName = variable.getKey();
                    if (extraNodes.containsKey(variableName) && !oldParentDataDependencyGraph.containsKey(variableName)) {
                        var lst = new ArrayList<>(variable.getValue());
                        oldParentDataDependencyGraph.put(variableName, lst);
                        continue;
                    }
                    var oldLst = oldParentDataDependencyGraph.get(variableName);
                    var lst = variable.getValue();
                    var intersect = CollectionUtils.intersection(oldLst, lst);
                    oldLst.addAll(CollectionUtils.subtract(lst, intersect));
                    oldParentDataDependencyGraph.put(variableName, oldLst);
                }
                DatadependenceLinks.put(dataDependencyGraphParent, oldParentDataDependencyGraph);
            } else {
                DatadependenceLinks.put(node, tmpDatadependenceLinks);
            }

        } else {
            DatadependenceLinks.put(node, variablesLinks);
        }

        used.add(node);

        for (var edge: newGraph.outgoingEdgesOf(node)) {
            var target = edge.getTarget();
            if (!used.contains(target)) {
                if (loopNodes.contains(node)) {
                    if (target.getKind() == BranchKind.CONVERGE) {
                        processBlocks(target, node, DatadependenceLinks.get(node), newGraph, stack, newPairs);
                    } else {
                        var newMap = new HashMap<String, List<ConditionalGraphNode>>();
                        for (var variable: variablesLinks.entrySet()) {
                            var lst = new ArrayList<>(variable.getValue());
                            newMap.put(variable.getKey(), lst);
                        }
                        processBlocks(target, node, newMap, newGraph, stack, newPairs);
                    }
                } else if (node.getKind() == BranchKind.BRANCH) {
                    if (target.getKind() == BranchKind.CONVERGE) {
                        processBlocks(target, node, variablesLinks, newGraph, stack, newPairs);
                    } else {
                        var newMap = new HashMap<String, List<ConditionalGraphNode>>();
                        for (var variable: variablesLinks.entrySet()) {
                            var lst = new ArrayList<>(variable.getValue());
                            newMap.put(variable.getKey(), lst);
                        }
                        processBlocks(target, node, newMap, newGraph, stack, newPairs);
                    }
                } else {
                    processBlocks(target, node, DatadependenceLinks.get(node), newGraph, stack, newPairs);
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
                    var comment = newCommend();
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


        for (var parameter: this.methodInfo.getParameters()) {
            var variableName = parameter.getReference().toString();
            var tmpNode = new ConditionalGraphNode(parameter, newGraph);
            extraNodes.put(variableName, tmpNode);
        }

        basicDfs(baseConditionalGraph.getStart(), newGraph);

        used = new HashSet<>();
        var stack = new Stack<ConditionalGraphNode>();
        var newEdges = new ArrayList<Pair<ConditionalGraphNode, ConditionalGraphNode>>();
        var variablesLinks = new HashMap<String, List<ConditionalGraphNode>>();
        processBlocks(newGraph.getStart(), null, variablesLinks, newGraph, stack, newEdges);
        newEdges.forEach(
            (Pair<ConditionalGraphNode, ConditionalGraphNode> pair) -> newGraph.addEdge(pair.first,
                pair.second,
                ConditionalEdgeType.DATADEPEDENCE));
        return newGraph;
    }

    private CtCommentImpl newCommend() {
        Factory factory;
        factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
        var comment = new CtCommentImpl();
        comment.getFactory().getEnvironment();
        comment.setContent("R" + (counter.getAndIncrement()));
        comment.setCommentType(CtComment.CommentType.BLOCK);
        comment.setFactory(factory);
        return comment;
    }

}
