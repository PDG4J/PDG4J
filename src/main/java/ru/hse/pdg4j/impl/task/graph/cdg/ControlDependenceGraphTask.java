package ru.hse.pdg4j.impl.task.graph.cdg;

import fr.inria.controlflow.ControlFlowGraph;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.hse.pdg4j.impl.task.graph.cfg.ControlFlowGraphTask;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraph;
import ru.hse.pdg4j.impl.task.graph.pdtg.PreprocessControlFlowTask;
import ru.hse.pdg4j.impl.task.util.IdleTask;
import ru.hse.pdg4j.impl.task.graph.pdtg.PostDominatorInfo;
import ru.hse.pdg4j.impl.task.graph.pdtg.PostDominatorTreeTask;
import spoon.reflect.declaration.CtMethod;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.api.PipelineTaskResult;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class ControlDependenceGraphTask implements PipelineTask<ControlDependenceGraphTask.Context> {

    public record Context(Map<CtMethod<?>, ConditionalGraph> graphMap) implements PipelineTaskContext {
    }

    private ControlDependenceGraphTask.Context context;

    private String methodName;

    public ControlDependenceGraphTask(String methodName) {
        this.methodName = methodName;
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
            if (!ctMethod.getSimpleName().equals(this.methodName)) {
                continue;
            }
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
}
