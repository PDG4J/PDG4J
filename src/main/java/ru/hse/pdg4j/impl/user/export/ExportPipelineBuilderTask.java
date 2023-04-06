package ru.hse.pdg4j.impl.user.export;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.task.NonContextualPipelineTask;
import ru.hse.pdg4j.api.user.BootstrapContext;
import ru.hse.pdg4j.impl.builder.PipelineGraphBuilder;
import ru.hse.pdg4j.impl.task.graph.cdg.AddRegionalNodesGraphExportTask;
import ru.hse.pdg4j.impl.task.graph.cdg.AddRegionalNodesTask;
import ru.hse.pdg4j.impl.task.graph.cdg.ControlDependenceGraphExportTask;
import ru.hse.pdg4j.impl.task.graph.cdg.DeleteAdditionalNodesExportTask;
import ru.hse.pdg4j.impl.task.graph.cfg.ControlFlowGraphExportTask;
import ru.hse.pdg4j.impl.task.graph.dfg.DataFlowExportTask;
import ru.hse.pdg4j.impl.task.graph.pdtg.PostDominatorTreeExportTask;

import java.io.File;
import ru.hse.pdg4j.impl.task.graph.pgd.ProgramDependenceGraphExportTask;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class ExportPipelineBuilderTask extends NonContextualPipelineTask {
    public ExportPipelineBuilderTask() {
        super("Export");
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        BootstrapContext bootstrapContext = context.getSharedContext(BootstrapContext.class);
        PipelineGraphBuilder builder = bootstrapContext.getAnalysisGraphBuilder();

        ExportOptions.Export exportOptions = bootstrapContext.getOptions().getExportOptions().getExport();
        if (exportOptions != null) {
            File exportRoot = new File(exportOptions.getExportRoot());
            for (ExportType exportType : exportOptions.getExportTypeSet()) {
                switch (exportType) {
                    case CDG -> builder.task(new ControlDependenceGraphExportTask(exportRoot));
                    case CFG -> builder.task(new ControlFlowGraphExportTask(exportRoot));
                    case PDTG -> builder.task(new PostDominatorTreeExportTask(exportRoot));
                    case DFG -> builder.task(new DataFlowExportTask(exportRoot));
                    case CDGR -> builder.task(new AddRegionalNodesGraphExportTask(exportRoot));
                    case CDGRA -> builder.task(new DeleteAdditionalNodesExportTask(exportRoot));
                    case PDG -> builder.task(new ProgramDependenceGraphExportTask(exportRoot));
                }
            }
        }

        return success();
    }
}
