package ru.hse.pdg4j.impl.task.graph.dfg;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowEdge;
import fr.inria.controlflow.ControlFlowGraph;
import fr.inria.controlflow.ControlFlowNode;
import fr.inria.dataflow.InitializedVariables;
import org.codehaus.plexus.util.CollectionUtils;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.impl.task.graph.cfg.ControlFlowGraphTask;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.support.reflect.code.CtCommentImpl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class DataFlowGraphTask implements PipelineTask<DataFlowGraphTask.Context> {
    public record Context(Map<CtMethod<?>, ControlFlowGraph> graphMap) implements PipelineTaskContext {
    }

    @Override
    public String getName() {
        return "Build Data Flow Graph";
    }

    @Override
    public DataFlowGraphTask.Context getContext() {
        return new Context(null);
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        var graph = context.getContext(ControlFlowGraphTask.Context.class).graphMap();
        graph.forEach((ctMethod, controlFlowGraph) -> {
            controlFlowGraph.simplify();
            for (ControlFlowEdge controlFlowEdge : controlFlowGraph.edgeSet()) {
                ControlFlowGraph newCfg = new ControlFlowGraph();

                ControlFlowNode begin = new ControlFlowNode(makeComment(ctMethod.getFactory()), newCfg, BranchKind.BEGIN);
                ControlFlowNode source = new ControlFlowNode(controlFlowEdge.getSourceNode().getStatement(), newCfg, controlFlowEdge.getSourceNode().getKind());
                ControlFlowNode target = new ControlFlowNode(controlFlowEdge.getTargetNode().getStatement(), newCfg, controlFlowEdge.getTargetNode().getKind());
                ControlFlowNode exit = new ControlFlowNode(makeComment(ctMethod.getFactory()), newCfg, BranchKind.EXIT);

                newCfg.addEdge(begin, source);
                newCfg.addEdge(source, target);
                newCfg.addEdge(target, exit);

                newCfg.simplify();

                InitializedVariables newVariables = new InitializedVariables();
                newVariables.setIncludeDefinedInNode(false);
                newVariables.run(newCfg.getExitNode());
                System.out.println(source.getStatement() + " -> " + target.getStatement() + ": " + CollectionUtils.subtract(newVariables.getDefined(), newVariables.getUsed()));
            }
        });
        return success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(ControlFlowGraphTask.class);
    }

    private CtCommentImpl makeComment(Factory factory) {
        var comment = new CtCommentImpl();
        comment.setContent("R" + (counter.getAndIncrement()));
        comment.setCommentType(CtComment.CommentType.BLOCK);
        comment.setFactory(factory);
        return comment;
    }

    private AtomicInteger counter = new AtomicInteger(0);
}
