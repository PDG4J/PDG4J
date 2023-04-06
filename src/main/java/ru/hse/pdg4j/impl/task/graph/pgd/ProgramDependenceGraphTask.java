package ru.hse.pdg4j.impl.task.graph.pgd;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.impl.task.graph.cdg.DeleteAdditionalNodesTask;
import ru.hse.pdg4j.impl.task.graph.dfg.DataFlowGraph;
import ru.hse.pdg4j.impl.task.graph.dfg.DataFlowGraphTask;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalEdgeType;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraph;
import ru.hse.pdg4j.impl.task.graph.pdtg.PreprocessControlFlowTask;
import spoon.reflect.declaration.CtMethod;

public class ProgramDependenceGraphTask implements PipelineTask<ProgramDependenceGraphTask.Context> {
    public record Context(Map<CtMethod<?>, ConditionalGraph> graphMap) implements PipelineTaskContext {
    }

    public ProgramDependenceGraphTask() {
    }

    @Override
    public String getName() {
        return "Create ProgramDependenceGraph";
    }

    private ProgramDependenceGraphTask.Context context;

    @Override
    public ProgramDependenceGraphTask.Context getContext() {
        return context;
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        Map<CtMethod<?>, ConditionalGraph> graphMap = new HashMap<>();

        var graph = context.getContext(DeleteAdditionalNodesTask.Context.class).graphMap();
        var dataFlowGraph = context.getContext(DataFlowGraphTask.Context.class).graphMap();
        graph.forEach((ctMethod, condtionalGraph) -> {
            var newGraph = new ConditionalGraph();

            for (var edge: condtionalGraph.edgeSet()) {
                newGraph.addEdge(edge.getSource(), edge.getTarget(),  edge.getType(), edge.isBackEdge());
            }

            for (var edge: dataFlowGraph.get(ctMethod).edgeSet()) {
                if (edge.getType() == ConditionalEdgeType.DATADEPEDENCE) {
                    newGraph.addEdge(edge.getSource(), edge.getTarget(),  edge.getType(), edge.isBackEdge());
                }
            }

            graphMap.put(ctMethod, newGraph);
        });

        this.context = new Context(graphMap);
        return success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(DataFlowGraphTask.class, DeleteAdditionalNodesTask.class);
    }

}
