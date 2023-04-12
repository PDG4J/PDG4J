package ru.hse.pdg4j.impl.task.graph.dfg;

import java.util.HashMap;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraph;
import ru.hse.pdg4j.impl.task.graph.pdtg.PreprocessControlFlowTask;
import spoon.reflect.declaration.CtMethod;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class DataFlowGraphTask implements PipelineTask<DataFlowGraphTask.Context> {
    public record Context(Map<CtMethod<?>, ConditionalGraph> graphMap) implements PipelineTaskContext {
    }

    public DataFlowGraphTask() {
    }

    @Override
    public String getName() {
        return "Build Data Flow Graph";
    }

    private DataFlowGraphTask.Context context;

    @Override
    public DataFlowGraphTask.Context getContext() {
        return context;
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        Map<CtMethod<?>, ConditionalGraph> graphMap = new HashMap<>();

        var graph = context.getContext(PreprocessControlFlowTask.Context.class).graphMap();
        graph.forEach((ctMethod, condtionalGraph) -> {
            var newGraph = new DataFlowGraph(condtionalGraph, ctMethod);

            graphMap.put(ctMethod, newGraph.getDataFlowGraph());
        });

        this.context = new Context(graphMap);
        return success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(PreprocessControlFlowTask.class);
    }

}
