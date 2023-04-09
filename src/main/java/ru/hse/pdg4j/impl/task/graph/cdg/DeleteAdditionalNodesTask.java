package ru.hse.pdg4j.impl.task.graph.cdg;

import fr.inria.controlflow.BranchKind;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraph;
import spoon.reflect.declaration.CtMethod;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class DeleteAdditionalNodesTask implements PipelineTask<DeleteAdditionalNodesTask.Context> {

    private DeleteAdditionalNodesTask.Context context;

    public DeleteAdditionalNodesTask() {
    }

    @Override
    public String getName() {
        return "Delete from CDG additional nodes";
    }

    @Override
    public DeleteAdditionalNodesTask.Context getContext() {
        return context;
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        Map<CtMethod<?>, ConditionalGraph> controlFlowGraphMap = new HashMap<>();
        var controlDependenceContext = context.getContext(AddRegionalNodesTask.Context.class);

        for (Map.Entry<CtMethod<?>, ConditionalGraph> entry : controlDependenceContext.graphMap().entrySet()) {
            CtMethod<?> ctMethod = entry.getKey();
            var info = entry.getValue();
            var graph = new ConditionalGraph();

            var kindToDelete = List.of(BranchKind.BEGIN, BranchKind.EXIT, BranchKind.BLOCK_BEGIN, BranchKind.BLOCK_END, BranchKind.CONVERGE);
            for (var edge : info.edgeSet()) {
                if (kindToDelete.contains(edge.getSource().getKind()) || kindToDelete.contains(edge.getTarget().getKind())) {
                    continue;
                }
                graph.addEdge(edge);
            }
            controlFlowGraphMap.put(ctMethod, graph);
        }
        this.context = new DeleteAdditionalNodesTask.Context(controlFlowGraphMap);

        return success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(AddRegionalNodesTask.class);
    }

    public record Context(Map<CtMethod<?>, ConditionalGraph> graphMap) implements PipelineTaskContext {
    }
}
