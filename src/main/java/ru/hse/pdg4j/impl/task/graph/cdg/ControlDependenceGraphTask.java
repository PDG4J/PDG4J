package ru.hse.pdg4j.impl.task.graph.cdg;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraph;
import ru.hse.pdg4j.impl.task.graph.pdtg.PostDominatorTreeTask;
import ru.hse.pdg4j.impl.task.graph.pdtg.PreprocessControlFlowTask;
import spoon.reflect.declaration.CtMethod;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class ControlDependenceGraphTask implements PipelineTask<ControlDependenceGraphTask.Context> {

    private ControlDependenceGraphTask.Context context;

    public ControlDependenceGraphTask() {
    }

    @Override
    public String getName() {
        return "Create CDG";
    }

    @Override
    public ControlDependenceGraphTask.Context getContext() {
        return context;
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        Map<CtMethod<?>, ConditionalGraph> controlFlowGraphMap = new HashMap<>();
        var postDominatorContext = context.getContext(PostDominatorTreeTask.Context.class);
        var basicControlFlows = context.getContext(PreprocessControlFlowTask.Context.class).graphMap();

        for (Map.Entry<CtMethod<?>, ConditionalGraph> entry : postDominatorContext.infoMap().entrySet()) {
            CtMethod<?> ctMethod = entry.getKey();
            var info = entry.getValue();
            var graph = new ControlDependenceGraph(basicControlFlows.get(ctMethod), info);
            controlFlowGraphMap.put(ctMethod, graph.getControlDependenceGraph());
        }
        this.context = new ControlDependenceGraphTask.Context(controlFlowGraphMap);

        return success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(PostDominatorTreeTask.class);
    }

    public record Context(Map<CtMethod<?>, ConditionalGraph> graphMap) implements PipelineTaskContext {
    }
}
