package ru.hse.pdg4j.impl.task.graph.pdtg;

import fr.inria.controlflow.ControlFlowGraph;
import fr.inria.controlflow.ControlFlowNode;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.impl.task.graph.cfg.ControlFlowGraphTask.Context;
import ru.hse.pdg4j.impl.task.util.IdleTask;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.impl.task.graph.cfg.ControlFlowGraphTask;
import spoon.reflect.declaration.CtMethod;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class PostDominatorTreeTask implements PipelineTask<PostDominatorTreeTask.Context> {
    public record Context(Map<CtMethod<?>, PostDominatorInfo> infoMap) implements PipelineTaskContext {
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
        Map<CtMethod<?>, PostDominatorInfo> controlFlowGraphMap = new HashMap<>();

        var graphContext = context.getContext(PreprocessControlFlowTask.Context.class);
        for (Map.Entry<CtMethod<?>, ControlFlowGraph> entry : graphContext.graphMap().entrySet()) {
            CtMethod<?> ctMethod = entry.getKey();
            if (!ctMethod.getSimpleName().equals(this.methodName)) {
                continue;
            }
            ControlFlowGraph controlFlowGraph = entry.getValue();
            PostDominatorTreeGraph postDominatorTreeGraph = new PostDominatorTreeGraph(true);
            var edges = controlFlowGraph.edgeSet();
            for (var edge: edges) {
                postDominatorTreeGraph.addEdge(edge.getTargetNode(), edge.getSourceNode());
            }

            postDominatorTreeGraph.createPostDominatorTree();
            var info = postDominatorTreeGraph.getPostDominatorInfo();
            controlFlowGraphMap.put(ctMethod, info);
        }
        this.context = new PostDominatorTreeTask.Context(controlFlowGraphMap);

        return success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(PreprocessControlFlowTask.class);
    }
}