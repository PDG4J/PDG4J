package ru.hse.pdg4j.impl.user.check;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.task.NonContextualPipelineTask;
import ru.hse.pdg4j.api.user.BootstrapContext;
import ru.hse.pdg4j.impl.builder.PipelineGraphBuilder;
import ru.hse.pdg4j.impl.task.basic.MethodExtractionTask;
import ru.hse.pdg4j.impl.task.graph.cdg.AddRegionalNodesTask;
import ru.hse.pdg4j.impl.task.graph.cdg.ControlDependenceGraphTask;
import ru.hse.pdg4j.impl.task.graph.cdg.DeleteAdditionalNodesTask;
import ru.hse.pdg4j.impl.task.graph.cfg.ControlFlowGraphTask;
import ru.hse.pdg4j.impl.task.graph.dfg.DataFlowGraphTask;
import ru.hse.pdg4j.impl.task.graph.pdtg.PostDominatorTreeTask;
import ru.hse.pdg4j.impl.task.graph.pdtg.PreprocessControlFlowTask;
import ru.hse.pdg4j.impl.task.graph.pgd.ProgramDependenceGraphTask;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class MakeStructuresPipelineBuilderTask extends NonContextualPipelineTask {
    public MakeStructuresPipelineBuilderTask() {
        super("Make");
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        BootstrapContext bootstrapContext = context.getSharedContext(BootstrapContext.class);
        PipelineGraphBuilder builder = bootstrapContext.getAnalysisGraphBuilder();
        builder.task(new MethodExtractionTask())
                .task(new ControlFlowGraphTask())
                .task(new PreprocessControlFlowTask())
                .task(new PostDominatorTreeTask())
                .task(new ControlDependenceGraphTask())
                .task(new AddRegionalNodesTask())
                .task(new DeleteAdditionalNodesTask())
                .task(new DataFlowGraphTask())
                .task(new ProgramDependenceGraphTask());
        return success();
    }
}
