package ru.hse.pdg4j.impl.task.graph.cfg;

import fr.inria.controlflow.ControlFlowBuilder;
import fr.inria.controlflow.ControlFlowGraph;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.impl.task.basic.MethodExtractionTask;
import spoon.reflect.declaration.CtMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class ControlFlowGraphTask implements PipelineTask<ControlFlowGraphTask.Context> {
    record Context(Map<CtMethod<?>, ControlFlowGraph> graphMap) implements PipelineTaskContext {
    }

    public ControlFlowGraphTask() {
    }

    private Context context;

    @Override
    public String getName() {
        return "Build CFG";
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        Map<CtMethod<?>, ControlFlowGraph> controlFlowGraphMap = new HashMap<>();
        var methods = context.getContext(MethodExtractionTask.Context.class).methods();
        for (CtMethod<?> method : methods) {
            controlFlowGraphMap.put(method, new ControlFlowBuilder().build(method));
        }

        this.context = new Context(controlFlowGraphMap);
        return success();
    }

    public List<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(MethodExtractionTask.class);
    }
}
