package ru.hse.pdg4j.impl.task.report;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.CheckReport;
import ru.hse.pdg4j.api.check.CheckReportExportStrategy;
import ru.hse.pdg4j.impl.task.util.IdleTask;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.failure;
import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class ReportExportStringTask implements PipelineTask<IdleTask.Context> {
    private CheckReportExportStrategy<String> strategy;
    private OutputStream outputStream;

    public ReportExportStringTask(CheckReportExportStrategy<String> strategy, OutputStream outputStream) {
        this.strategy = strategy;
        this.outputStream = outputStream;
    }

    @Override
    public String getName() {
        return "Export report as string";
    }

    @Override
    public IdleTask.Context getContext() {
        return new IdleTask.Context();
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        CheckReport report = context.getSharedContext(ReportContext.class).getCheckReport();
        try {
            outputStream.write(strategy.export(report).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            return failure("Failed to write report: " + e.getMessage());
        }
        return success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return Collections.singleton(ReportFulfilTask.class);
    }
}
