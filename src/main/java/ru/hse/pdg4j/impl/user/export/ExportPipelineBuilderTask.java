package ru.hse.pdg4j.impl.user.export;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.task.NonContextualPipelineTask;
import ru.hse.pdg4j.impl.builder.PipelineGraphBuilder;
import ru.hse.pdg4j.impl.task.graph.cfg.ControlDependenceGraphExportTask;
import ru.hse.pdg4j.impl.task.graph.cfg.ControlFlowGraphExportTask;
import ru.hse.pdg4j.impl.task.graph.pdtg.PostDominatorTreeExportTask;
import ru.hse.pdg4j.impl.user.BootstrapContext;

import java.io.File;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class ExportPipelineBuilderTask extends NonContextualPipelineTask {
    public ExportPipelineBuilderTask() {
        super("Export");
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        BootstrapContext bootstrapContext = context.getSharedContext(BootstrapContext.class);
        PipelineGraphBuilder builder = bootstrapContext.getBuilder();

        ExportOptions.Export exportOptions = bootstrapContext.getOptions().getExportOptions().getExport();
        if (exportOptions != null) {
            File exportRoot = new File(exportOptions.getExportRoot());
            for (ExportType exportType : exportOptions.getExportTypeSet()) {
                switch (exportType) {
                    case CDG -> builder.task(new ControlDependenceGraphExportTask(exportRoot));
                    case CFG -> builder.task(new ControlFlowGraphExportTask(exportRoot));
                    case PDTG -> builder.task(new PostDominatorTreeExportTask(exportRoot));
                }
            }
        }

        return success();
    }
}
