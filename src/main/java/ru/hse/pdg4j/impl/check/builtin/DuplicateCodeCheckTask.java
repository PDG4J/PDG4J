package ru.hse.pdg4j.impl.check.builtin;

import fr.inria.controlflow.BranchKind;
import org.codehaus.plexus.util.CollectionUtils;
import org.jgrapht.alg.util.Pair;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.check.task.CheckPipelineTask;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalEdgeType;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraph;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraphNode;
import ru.hse.pdg4j.impl.task.graph.pgd.ProgramDependenceGraphTask;
import spoon.reflect.declaration.CtMethod;

import java.util.*;
import java.util.Map.Entry;

public class DuplicateCodeCheckTask extends CheckPipelineTask {

    public DuplicateCodeCheckTask() {
        super("DuplicateCodeCheck");
    }

    public void pushDataDependence(DataDependenceTree tree, Integer level, String statement, List<Pair<String, Boolean>> dependencies) {
        for (var dependency: dependencies) {
            if (!tree.dependencies.containsKey(dependency.first)) {
                tree.dependencies.put(dependency.first, new Pair<>(null, null));
            }
            var pair = tree.dependencies.get(dependency.first);
            if (dependency.second) {
                if (pair.second == null) {
                    pair.second = new DataDependenceTree();
                    tree.dependencies.put(dependency.first, pair);
                }
                tree = pair.second;
            } else {
                if (pair.first == null) {
                    pair.first = new DataDependenceTree();
                    tree.dependencies.put(dependency.first, pair);
                }
                tree = pair.first;
            }
        }
        if (!tree.mapping.containsKey(level)) {
            tree.mapping.put(level, new ArrayList<>());
        }
        var lst = tree.mapping.get(level);
        lst.add(statement);
        tree.mapping.put(level, lst);
    }

    public boolean checkDataDependenciesTree(DataDependenceTree first, DataDependenceTree second) {
        if (first == null && second != null) {
            return false;
        }
        if (first != null && second == null) {
            return false;
        }
        if (first == null) {
            return true;
        }

        for (var level: first.mapping.entrySet()) {
            if (!second.mapping.containsKey(level.getKey())) {
                return false;
            }
            if (level.getValue().size() != second.mapping.get(level.getKey()).size()) {
                return false;
            }
            var intersect = CollectionUtils.intersection(level.getValue(), second.mapping.get(level.getKey()));
            if (intersect.size() != level.getValue().size()) {
                return false;
            }
        }

        for (var depencency: first.dependencies.entrySet()) {
            if (!second.dependencies.containsKey(depencency.getKey())) {
                return false;
            }
            if (!checkDataDependenciesTree(depencency.getValue().first, second.dependencies.get(depencency.getKey()).first)) {
                return false;
            }
            if (!checkDataDependenciesTree(depencency.getValue().second, second.dependencies.get(depencency.getKey()).second)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkDataDependencies(List<Pair<String, Pair<Integer, List<Pair<String, Boolean>>>>> first, List<Pair<String, Pair<Integer, List<Pair<String, Boolean>>>>> second) {
        DataDependenceTree tree1 = new DataDependenceTree();
        for (var info: first) {
            pushDataDependence(tree1, info.second.first,  info.first, info.second.second);
        }
        DataDependenceTree tree2 = new DataDependenceTree();
        for (var info: second) {
            pushDataDependence(tree2, info.second.first,  info.first, info.second.second);
        }
        return checkDataDependenciesTree(tree1, tree2);
    }

    public Map<String, ConditionalGraphNode> getParameters(ConditionalGraph graph) {
        var parameters = new HashMap<String, ConditionalGraphNode>();
        var start = graph.getStart();
        for (var variable : graph.vertexSet()) {
            if (graph.inDegreeOf(variable) == 0 && start != variable) {
                parameters.put(variable.getStatement().prettyprint(), variable);
            }
        }
        return parameters;
    }

    public boolean dfs(ConditionalGraphNode node, Set<ConditionalGraphNode> used,
                       Map<ConditionalGraphNode, List<ConditionalGraphNode>> graph,
                       Map<ConditionalGraphNode, ConditionalGraphNode> mapping) {
        if (used.contains(node)) {
            return false;
        }
        used.add(node);
        for (var variable : graph.get(node)) {
            if (!mapping.containsKey(variable) || dfs(mapping.get(variable), used, graph, mapping)) {
                mapping.put(variable, node);
                return true;
            }
        }
        return false;
    }

    public List<Pair<String, Pair<Integer, List<Pair<String, Boolean>>>>> getMapping(List<ConditionalGraphNode> lst,
                                                                                     Map<ConditionalGraphNode, Pair<Integer, List<Pair<String, Boolean>>>> funcInfo) {
        List<Pair<String, Pair<Integer, List<Pair<String, Boolean>>>>> ans = new ArrayList<>();
        for (var node: lst) {
            if (funcInfo.containsKey(node)) {
                ans.add(new Pair<>(node.toString().split("- ")[1], funcInfo.get(node)));
            }
        }
        return ans;
    }


    public boolean checkMethods(ConditionalGraphNode node1, ConditionalGraphNode node2,
                                Map<ConditionalGraphNode, Pair<Integer, List<Pair<String, Boolean>>>> funcInfo1,
                                Map<ConditionalGraphNode, Pair<Integer, List<Pair<String, Boolean>>>> funcInfo2,
                                ConditionalGraph graph1, ConditionalGraph graph2) {
        if (!Objects.equals(funcInfo1.get(node1).first, funcInfo2.get(node2).first)) {
            return false;
        }

        if (!node1.toString().split("- ")[1].equals(node2.toString().split("- ")[1])) {
            return false;
        }
        var conditions1 = funcInfo1.get(node1).second;
        var conditions2 = funcInfo2.get(node2).second;
        if (conditions1.size() != conditions2.size()) {
            return false;
        }
        var it = conditions1.iterator();
        var jt = conditions2.iterator();
        while (it.hasNext()) {
            var p1 = it.next();
            var p2 = jt.next();
            if (p1.first.equals(p2.first) && p1.second == p2.second) {
                continue;
            } else {
                return false;
            }
        }

        List<ConditionalGraphNode> inDependentNodes1 = new ArrayList<>();
        for (var edge : graph1.incomingEdgesOf(node1)) {
            inDependentNodes1.add(edge.getSource());
        }
        List<ConditionalGraphNode> inDependentNodes2 = new ArrayList<>();
        for (var edge : graph2.incomingEdgesOf(node2)) {
            inDependentNodes2.add(edge.getSource());
        }

        if (inDependentNodes1.size() != inDependentNodes2.size()) {
            return false;
        }

        var mapping1 = getMapping(inDependentNodes1, funcInfo1);
        var mapping2 = getMapping(inDependentNodes2, funcInfo2);

        if (!checkDataDependencies(mapping1, mapping2)) {
            return false;
        }

        List<ConditionalGraphNode> outDependentNodes1 = new ArrayList<>();
        for (var edge : graph1.outgoingEdgesOf(node1)) {
            outDependentNodes1.add(edge.getTarget());
        }
        List<ConditionalGraphNode> outDependentNodes2 = new ArrayList<>();
        for (var edge : graph2.outgoingEdgesOf(node2)) {
            outDependentNodes2.add(edge.getTarget());
        }

        if (outDependentNodes1.size() != outDependentNodes2.size()) {
            return false;
        }

        var mapping3 = getMapping(outDependentNodes1, funcInfo1);
        var mapping4 = getMapping(outDependentNodes2, funcInfo2);

        if (!checkDataDependencies(mapping3, mapping4)) {
            return false;
        }

        return true;
    }

    public void dfsMapNodes(ConditionalGraphNode node, Integer height, ConditionalGraph graph,
                            Set<ConditionalGraphNode> used, List<Pair<String, Boolean>> stack,
                            Map<ConditionalGraphNode, Pair<Integer, List<Pair<String, Boolean>>>> mapping) {
        used.add(node);
        var lst = new ArrayList<>(stack);
        mapping.put(node, new Pair<>(height, lst));
        for (var edge : graph.outgoingEdgesOf(node)) {
            var target = edge.getTarget();
            if (used.contains(target) || edge.getType() == ConditionalEdgeType.DATADEPEDENCE) {
                continue;
            }
            if (node.getKind() == BranchKind.BRANCH) {
                stack.add(new Pair<>(node.toString().split("- ")[1],
                        edge.getType() == ConditionalEdgeType.TRUE));
            }
            dfsMapNodes(target, height + 1, graph, used, stack, mapping);
            if (node.getKind() == BranchKind.BRANCH) {
                stack.remove(stack.size() - 1);
            }
        }
    }

    public Map<ConditionalGraphNode, Pair<Integer, List<Pair<String, Boolean>>>> mapNodes(
            ConditionalGraph graph) {
        Map<ConditionalGraphNode, Pair<Integer, List<Pair<String, Boolean>>>> map = new HashMap<>();
        var start = graph.getStart();
        dfsMapNodes(start, 0, graph, new HashSet<>(), new ArrayList<>(), map);
        return map;
    }


    public boolean checkMethods(CtMethod<?> method1, ConditionalGraph graph1, CtMethod<?> method2,
                                ConditionalGraph graph2) {
        if (graph1.vertexSet().size() != graph2.vertexSet().size()) {
            return false;
        }
        if (graph1.edgeSet().size() != graph2.edgeSet().size()) {
            return false;
        }
        var parameters1 = getParameters(graph1);
        var parameters2 = getParameters(graph2);

        if (parameters1.size() != parameters2.size()) {
            return false;
        }
        Map<ConditionalGraphNode, List<ConditionalGraphNode>> graph = new HashMap<>();
        Map<ConditionalGraphNode, ConditionalGraphNode> mapping = new HashMap<>();

        graph.put(graph1.getStart(), List.of(graph2.getStart()));

        for (var variableName : parameters1.keySet()) {
            if (!parameters2.containsKey(variableName)) {
                return false;
            } else {
                graph.put(parameters1.get(variableName), List.of(parameters2.get(variableName)));
                mapping.put(parameters2.get(variableName), parameters1.get(variableName));
            }
        }

        var funcInfo1 = mapNodes(graph1);
        for (var parameter : parameters1.entrySet()) {
            funcInfo1.put(parameter.getValue(), new Pair<>(0, new ArrayList<>()));
        }
        Map<Integer, List<ConditionalGraphNode>> levelInfo1 = new HashMap<>();
        for (var node : funcInfo1.entrySet()) {
            if (!levelInfo1.containsKey(node.getValue().first)) {
                levelInfo1.put(node.getValue().first, new ArrayList<>());
            }
            var lst = levelInfo1.get(node.getValue().first);
            lst.add(node.getKey());
            levelInfo1.put(node.getValue().first, lst);
        }

        var funcInfo2 = mapNodes(graph2);
        for (var parameter : parameters2.entrySet()) {
            funcInfo2.put(parameter.getValue(), new Pair<>(0, new ArrayList<>()));
        }
        Map<Integer, List<ConditionalGraphNode>> levelInfo2 = new HashMap<>();
        for (var node : funcInfo2.entrySet()) {
            if (!levelInfo2.containsKey(node.getValue().first)) {
                levelInfo2.put(node.getValue().first, new ArrayList<>());
            }
            var lst = levelInfo2.get(node.getValue().first);
            lst.add(node.getKey());
            levelInfo2.put(node.getValue().first, lst);
        }

        if (levelInfo1.size() != levelInfo2.size()) {
            return false;
        }
        for (var level : levelInfo1.entrySet()) {
            var variables1 = level.getValue();
            var variables2 = levelInfo2.get(level.getKey());
            for (var v1 : variables1) {
                if (graph.containsKey(v1)) {
                    continue;
                }
                for (var v2 : variables2) {
                    var variableName2 = v2.getStatement().prettyprint();
                    if (parameters2.containsKey(variableName2)) {
                        continue;
                    }
                    if (checkMethods(v1, v2, funcInfo1, funcInfo2, graph1, graph2)) {
                        if (!graph.containsKey(v1)) {
                            graph.put(v1, new ArrayList<>());
                        }
                        var lst = graph.get(v1);
                        lst.add(v2);
                        graph.put(v1, lst);
                    }
                }
            }
        }

        var used = new HashSet<ConditionalGraphNode>();
        for (var variable : graph1.vertexSet()) {
            used = new HashSet<>();
            if (!graph.containsKey(variable)) {
                return false;
            }
            dfs(variable, used, graph, mapping);
        }

        if (mapping.size() != graph1.vertexSet().size()) {
            return false;
        }

        return true;
    }

    @Override
    public void perform(PipelineContext pipelineContext) {
        var graph = pipelineContext.getContext(ProgramDependenceGraphTask.Context.class).graphMap();
        var end = graph.entrySet().size();
        var it = graph.entrySet().iterator();
        int count = 0;
        while (it.hasNext()) {
            var en1 = it.next();
            count++;
            if (count == end) {
                break;
            }
            var jt = graph.entrySet().iterator();
            Entry<CtMethod<?>, ConditionalGraph> en2 = null;
            var tmpCount = 0;
            while (jt.hasNext()){
                en2 = jt.next();
                tmpCount++;
                if (tmpCount <= count) {
                    continue;
                }
                if (checkMethods(en1.getKey(), en1.getValue(), en2.getKey(), en2.getValue())) {
                    warning(
                            "There is possibility that " + en1.getKey().getSimpleName() + " duplicates "
                                    + en2.getKey().getSimpleName());
                }
                if (tmpCount == end) {
                    break;
                }
            }
        }
        success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(ProgramDependenceGraphTask.class);
    }
}