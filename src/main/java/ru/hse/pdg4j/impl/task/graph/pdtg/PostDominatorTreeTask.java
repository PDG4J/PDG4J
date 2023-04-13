package ru.hse.pdg4j.impl.task.graph.pdtg;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import spoon.reflect.declaration.CtMethod;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class PostDominatorTreeTask implements PipelineTask<PostDominatorTreeTask.Context> {

    private Context context;

    public PostDominatorTreeTask() {
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

    public record Context(Map<CtMethod<?>, ConditionalGraph> infoMap) implements PipelineTaskContext {
    }
}