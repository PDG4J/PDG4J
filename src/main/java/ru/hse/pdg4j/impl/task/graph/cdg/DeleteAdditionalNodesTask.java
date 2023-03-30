package ru.hse.pdg4j.impl.task.graph.cdg;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

import fr.inria.controlflow.BranchKind;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraph;
import spoon.reflect.declaration.CtMethod;

public class DeleteAdditionalNodesTask implements PipelineTask<DeleteAdditionalNodesTask.Context> {

    public record Context(Map<CtMethod<?>, ConditionalGraph> graphMap) implements PipelineTaskContext {
    }

    private DeleteAdditionalNodesTask.Context context;

    private String methodName;

    public DeleteAdditionalNodesTask(String methodName) {
        this.methodName = methodName;
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
            if (!ctMethod.getSimpleName().equals(this.methodName)) {
                continue;
            }
            var info = entry.getValue();
            var graph = new ConditionalGraph();

            var kindToDelete = List.of(BranchKind.BLOCK_BEGIN, BranchKind.BLOCK_END, BranchKind.CONVERGE);
            for (var edge: info.edgeSet()) {
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
}
