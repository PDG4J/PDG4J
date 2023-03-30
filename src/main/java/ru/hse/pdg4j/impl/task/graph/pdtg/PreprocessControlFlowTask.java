package ru.hse.pdg4j.impl.task.graph.pdtg;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowGraph;
import fr.inria.controlflow.ControlFlowNode;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.impl.task.util.IdleTask;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.impl.task.graph.cfg.ControlFlowGraphTask;
import spoon.reflect.code.CtComment.CommentType;
import spoon.reflect.declaration.CtMethod;
import spoon.support.reflect.code.CtCommentImpl;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class PreprocessControlFlowTask implements PipelineTask<PreprocessControlFlowTask.Context> {
    public record Context(Map<CtMethod<?>, ConditionalGraph> graphMap) implements PipelineTaskContext {
    }

    private Context context;

    private String methodName;

    public PreprocessControlFlowTask(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String getName() {
        return "Preprocess Control Flow";
    }

    @Override
    public PreprocessControlFlowTask.Context getContext() {
        return context;
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        Map<CtMethod<?>, ConditionalGraph> conditionalGraphMap = new HashMap<>();

        var graphContext = context.getContext(ControlFlowGraphTask.Context.class);
        for (Map.Entry<CtMethod<?>, ControlFlowGraph> entry : graphContext.graphMap().entrySet()) {
            CtMethod<?> ctMethod = entry.getKey();
            if (!ctMethod.getSimpleName().equals(this.methodName)) {
                continue;
            }
            ControlFlowGraph controlFlowGraph = entry.getValue();
            ConditionalGraph graph = new ConditionalGraph();

            graph.build(controlFlowGraph);

            var comment = new CtCommentImpl();
            comment.setContent("ENTRY");
            comment.setCommentType(CommentType.BLOCK);

            var ENTRYNODE = new ConditionalGraphNode(comment, graph, BranchKind.BRANCH);
            graph.addVertex(ENTRYNODE);
            graph.addEdge(ENTRYNODE, graph.getStart());
            graph.addEdge(ENTRYNODE, graph.getEnd());

            conditionalGraphMap.put(ctMethod, graph);
        }
        this.context = new PreprocessControlFlowTask.Context(conditionalGraphMap);

        return success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(ControlFlowGraphTask.class);
    }
}