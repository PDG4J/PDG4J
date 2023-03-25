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
import ru.hse.pdg4j.impl.task.graph.cfg.ControlFlowGraphTask.Context;
import ru.hse.pdg4j.impl.task.util.IdleTask;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.impl.task.graph.cfg.ControlFlowGraphTask;
import spoon.reflect.code.CtComment.CommentType;
import spoon.reflect.declaration.CtMethod;
import spoon.support.reflect.code.CtCommentImpl;
import spoon.support.reflect.code.CtLocalVariableImpl;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class PreprocessControlFlowTask implements PipelineTask<PreprocessControlFlowTask.Context> {
    public record Context(Map<CtMethod<?>, ControlFlowGraph> graphMap) implements PipelineTaskContext {
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
        Map<CtMethod<?>, ControlFlowGraph> controlFlowGraphMap = new HashMap<>();

        var graphContext = context.getContext(ControlFlowGraphTask.Context.class);
        for (Map.Entry<CtMethod<?>, ControlFlowGraph> entry : graphContext.graphMap().entrySet()) {
            CtMethod<?> ctMethod = entry.getKey();
            if (!ctMethod.getSimpleName().equals(this.methodName)) {
                continue;
            }
            ControlFlowGraph controlFlowGraph = entry.getValue();
            PostDominatorTreeGraph postDominatorTreeGraph = new PostDominatorTreeGraph(false);
            var edges = controlFlowGraph.edgeSet();
            for (var edge: edges) {
                if (edge.getSourceNode().getStatement() != null)
                    System.out.println(edge.getSourceNode().getStatement().getClass());
                postDominatorTreeGraph.addEdge(edge.getSourceNode(), edge.getTargetNode());
            }
            var comment = new CtCommentImpl();
            comment.setContent("ENTRY");
            comment.setCommentType(CommentType.BLOCK);
            var ENTRYNODE = new ControlFlowNode(comment, postDominatorTreeGraph, BranchKind.STATEMENT);
            postDominatorTreeGraph.addVertex(ENTRYNODE);
            postDominatorTreeGraph.addEdge(ENTRYNODE, postDominatorTreeGraph.getStart());
            postDominatorTreeGraph.addEdge(ENTRYNODE, postDominatorTreeGraph.getEnd());

            controlFlowGraphMap.put(ctMethod, postDominatorTreeGraph);
        }
        this.context = new PreprocessControlFlowTask.Context(controlFlowGraphMap);

        return success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(ControlFlowGraphTask.class);
    }
}