package ru.hse.pdg4j.impl.user.report;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.task.NonContextualPipelineTask;
import ru.hse.pdg4j.api.user.BootstrapContext;
import ru.hse.pdg4j.impl.check.SequentialCheckReport;
import ru.hse.pdg4j.impl.task.report.ReportExportStringTask;
import ru.hse.pdg4j.impl.task.report.ReportFulfilFilterTask;
import ru.hse.pdg4j.impl.task.report.ReportFulfilTask;

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
        bootstrapContext.getAnalysisGraphBuilder().task(fulfilTask);

        String reportFile = report.getReportFile();
        if (reportFile != null && !reportFile.isEmpty() && !reportFile.contains(".")) {
            reportFile = reportFile + "." + report.getExportType().getExtension();
        }

        OutputStream stream;
        if (reportFile == null || reportFile.isEmpty()) {
            stream = System.out;
        } else {
            try {
                stream = new FileOutputStream(reportFile);
            } catch (FileNotFoundException e) {
                return failure("Could not find the specified file: " + e.getMessage());
            }
        }

        ReportExportStringTask exportStringTask = new ReportExportStringTask(report.getExportType().getStrategy(), stream);
        bootstrapContext.getAnalysisGraphBuilder().task(exportStringTask, fulfilTask.getName());

        return success();
    }
}
