package ru.hse.pdg4j.impl.task.graph.pdtg;

import fr.inria.controlflow.ControlFlowGraph;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.impl.task.util.IdleTask;
import ru.hse.pdg4j.api.PipelineTaskResult;
import spoon.reflect.declaration.CtMethod;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class PostDominatorTreeTask implements PipelineTask<PostDominatorTreeTask.Context> {
    public record Context(Map<CtMethod<?>, ConditionalGraph> infoMap) implements PipelineTaskContext {
    }

    private Context context;

    private String methodName;

    public PostDominatorTreeTask(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String getName() {
        return "Create PDTG";
    }

    @Override
    public PostDominatorTreeTask.Context getContext() {
        return context;
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        Map<CtMethod<?>, ConditionalGraph> conditionalGraphMap = new HashMap<>();

        var graphContext = context.getContext(PreprocessControlFlowTask.Context.class);
        for (Map.Entry<CtMethod<?>, ConditionalGraph> entry : graphContext.graphMap().entrySet()) {
            CtMethod<?> ctMethod = entry.getKey();
            if (!ctMethod.getSimpleName().equals(this.methodName)) {
                continue;
            }
            ConditionalGraph conditionalGraph = entry.getValue();
            PostDominatorTree postDominatorTreeGraph = new PostDominatorTree();

            var edges = conditionalGraph.edgeSet();
            for (var edge: edges) {
                postDominatorTreeGraph.addEdge(edge.getTarget(), edge.getSource(), edge.getType(), edge.isBackEdge());
            }

            postDominatorTreeGraph.createPostDominatorTree();
            var info = postDominatorTreeGraph.getPostDominatorInfo();
            conditionalGraphMap.put(ctMethod, info);
        }
        this.context = new PostDominatorTreeTask.Context(conditionalGraphMap);

        return success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(PreprocessControlFlowTask.class);
    }
}