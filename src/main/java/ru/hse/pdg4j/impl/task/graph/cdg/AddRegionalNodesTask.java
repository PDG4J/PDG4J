package ru.hse.pdg4j.impl.task.graph.cdg;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.impl.task.graph.pdtg.ConditionalGraph;
import ru.hse.pdg4j.impl.task.graph.pdtg.PostDominatorTreeTask;
import ru.hse.pdg4j.impl.task.graph.pdtg.PreprocessControlFlowTask;
import spoon.reflect.declaration.CtMethod;

public class AddRegionalNodesTask implements PipelineTask<AddRegionalNodesTask.Context> {

    public record Context(Map<CtMethod<?>, ConditionalGraph> graphMap) implements PipelineTaskContext {
    }

    private AddRegionalNodesTask.Context context;

    private String methodName;

    public AddRegionalNodesTask(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String getName() {
        return "Create CDG with additional regional nodes";
    }

    @Override
    public AddRegionalNodesTask.Context getContext() {
        return context;
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        Map<CtMethod<?>, ConditionalGraph> controlFlowGraphMap = new HashMap<>();
        var controlDependenceContext = context.getContext(ControlDependenceGraphTask.Context.class);

        for (Map.Entry<CtMethod<?>, ConditionalGraph> entry : controlDependenceContext.graphMap().entrySet()) {
            CtMethod<?> ctMethod = entry.getKey();
            if (!ctMethod.getSimpleName().equals(this.methodName)) {
                continue;
            }
            var info = entry.getValue();

            var graph = new AddRegionalNodesGraph(info);



            controlFlowGraphMap.put(ctMethod, graph.getControlDependenceGraph());
        }
        this.context = new AddRegionalNodesTask.Context(controlFlowGraphMap);

        return success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(ControlDependenceGraphTask.class);
    }
}
