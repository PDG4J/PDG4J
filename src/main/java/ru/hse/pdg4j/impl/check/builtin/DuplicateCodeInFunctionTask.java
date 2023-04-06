package ru.hse.pdg4j.impl.check.builtin;

import fr.inria.controlflow.BranchKind;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.codehaus.plexus.util.CollectionUtils;
import org.jgrapht.alg.util.Pair;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.check.task.CheckPipelineTask;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalEdge;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalEdgeType;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraph;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraphNode;
import ru.hse.pdg4j.impl.task.graph.pgd.ProgramDependenceGraphTask;

import java.util.Collection;
import java.util.List;
import spoon.reflect.declaration.CtMethod;

public class DuplicateCodeInFunctionTask extends CheckPipelineTask {

    public DuplicateCodeInFunctionTask() {
        super("DuplicateCodeInFunctionTask");
    }

    public Pair<Integer, Integer> dfs(ConditionalGraphNode node, Set<ConditionalGraphNode> used, ConditionalGraph graph, Map<ConditionalGraphNode, Pair<Integer, Integer>> answer) {
        int count = 1, length = 0;
        used.add(node);
        for (var edge: graph.outgoingEdgesOf(node)) {
            if (edge.getType() != ConditionalEdgeType.DATADEPEDENCE && !used.contains(edge.getTarget())) {
                var pair = dfs(edge.getTarget(), used, graph, answer);
                count += pair.first;
                length = Math.max(length, pair.second);
            }
        }
        answer.put(node, new Pair<>(count, length + 1));
        return answer.get(node);
    }

    public Map<String, ConditionalGraphNode> getMappingOfChild(ConditionalGraphNode node, ConditionalGraph graph) {
        var nodes = graph.outgoingEdgesOf(node);
        Map<String, ConditionalGraphNode> map = new HashMap<>();
        for (var edge: nodes) {
            var tmpNode = edge.getTarget();
            var statement = tmpNode.toString().split("- ")[1];
            if (map.containsKey(statement)) {
                return null;
            }
            map.put(tmpNode.toString().split("- ")[1], tmpNode);
        }
        return map;
    }

    public List<ConditionalGraphNode> getDataDependencies(ConditionalGraphNode node, ConditionalGraph graph) {
        List<ConditionalGraphNode> lst = new ArrayList<>();
        for (var edge: graph.incomingEdgesOf(node)) {
            if (edge.getType() == ConditionalEdgeType.DATADEPEDENCE) {
                lst.add(edge.getSource());
            }
        }
        return lst;
    }

    public boolean checkNodes(ConditionalGraphNode nodeFirst, ConditionalGraphNode nodeSecond, ConditionalGraph graph, Map<ConditionalGraphNode, Pair<Integer, Integer>> mapping) {
        var p1 = mapping.get(nodeFirst);
        var p2 = mapping.get(nodeSecond);
        if (!Objects.equals(p1.first, p2.first) || !Objects.equals(p1.second, p2.second)) {
            return false;
        }
        if (graph.outDegreeOf(nodeFirst) != graph.outDegreeOf(nodeSecond)) {
            return false;
        }
        var edgesGroup1 = graph.outgoingEdgesOf(nodeFirst).stream().collect(
            Collectors.groupingBy(ConditionalEdge::getType, Collectors.toList()));

        var edgesGroup2 = graph.outgoingEdgesOf(nodeSecond).stream().collect(
            Collectors.groupingBy(ConditionalEdge::getType, Collectors.toList()));

        for (var entry: edgesGroup1.entrySet()) {
            var node1 = ((ConditionalEdge) entry.getValue().toArray()[0]).getTarget();
            var map1 = getMappingOfChild(node1, graph);

            var node2 = ((ConditionalEdge) edgesGroup2.get(entry.getKey()).toArray()[0]).getTarget();
            var map2 = getMappingOfChild(node2, graph);

            if (map1 == null || map2 == null) {
                return false;
            }
            if (map1.size() != map2.size()) {
                return false;
            }

            for (var statement: map1.keySet()) {
                if (!map2.containsKey(statement)) {
                    return false;
                }
                var dp1 = getDataDependencies(map1.get(statement), graph);
                var dp2 = getDataDependencies(map2.get(statement), graph);
                if (dp1.size() != dp2.size()) {
                    return false;
                }
                var intersect = CollectionUtils.intersection(dp1, dp2);
                if (intersect.size() != dp1.size()) {
                    return false;
                }
                var tmpNode1 = map1.get(statement);
                var tmpNode2 = map2.get(statement);
                if (tmpNode1.getKind() != tmpNode2.getKind()) {
                    return false;
                }
                if (tmpNode1.getKind() == BranchKind.BRANCH && !checkNodes(tmpNode1, tmpNode2, graph, mapping)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void perform(PipelineContext pipelineContext) {
        var graphs = pipelineContext.getContext(ProgramDependenceGraphTask.Context.class).graphMap();
        for (var entry: graphs.entrySet()) {
            var graph = entry.getValue();
            var lst = new ArrayList<ConditionalGraphNode>();
            for (var vertex: graph.vertexSet()) {
                if (vertex.getKind() == BranchKind.BRANCH) {
                    lst.add(vertex);
                }
            }
            Map<ConditionalGraphNode, Pair<Integer, Integer>> mapping = new HashMap<>();
            dfs(graph.getStart(), new HashSet<>(), graph, mapping);
            for (int i = 0; i < lst.size(); ++i) {
                for (int j = i + 1; j < lst.size(); ++j) {
                    var node1 = lst.get(i);
                    var node2 = lst.get(j);
                    if (checkNodes(node1, node2, graph, mapping) && node1.getStatement() != null && node2.getStatement() != null) {
                        warning("In method " + entry.getKey().getSimpleName() + " " + node1.getStatement().getPosition() + " duplicates to " + node2.getStatement().getPosition());
                    }
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