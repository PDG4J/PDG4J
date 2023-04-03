package ru.hse.pdg4j.impl.user.report;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.task.NonContextualPipelineTask;
import ru.hse.pdg4j.impl.check.SequentialCheckReport;
import ru.hse.pdg4j.impl.task.report.ReportExportStringTask;
import ru.hse.pdg4j.impl.task.report.ReportFulfilFilterTask;
import ru.hse.pdg4j.impl.task.report.ReportFulfilTask;
import ru.hse.pdg4j.impl.user.BootstrapContext;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.failure;
import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class ReportPipelineBuilderTask extends NonContextualPipelineTask {
    public ReportPipelineBuilderTask() {
        super("Report");
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        BootstrapContext bootstrapContext = context.getSharedContext(BootstrapContext.class);
        ReportOptions.Report report = bootstrapContext.getOptions().getReportOptions().getReport();
        if (report == null || !report.isWriteReport()) {
            return success();
        }

        ReportFulfilTask fulfilTask = new ReportFulfilFilterTask(new SequentialCheckReport(), Collections.emptyList());
        bootstrapContext.getBuilder().task(fulfilTask);

        OutputStream stream;
        if (report.getReportFile().isEmpty()) {
            stream = System.out;
        } else {
            try {
                stream = new FileOutputStream(report.getReportFile());
            } catch (FileNotFoundException e) {
                return failure("Could not find the specified file: " + e.getMessage());
            }
        }

        ReportExportStringTask exportStringTask = new ReportExportStringTask(report.getExportType().getStrategy(), stream);
        bootstrapContext.getBuilder().task(exportStringTask, fulfilTask.getName());

        return success();
    }
}
