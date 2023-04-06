package ru.hse.pdg4j.impl.task.report;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.CheckReport;
import ru.hse.pdg4j.api.check.task.CheckPipelineTask;
import ru.hse.pdg4j.impl.task.util.IdleTask;

import java.util.Collection;
import java.util.List;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public abstract class ReportFulfilTask implements PipelineTask<IdleTask.Context> {
    private final CheckReport report;

    public ReportFulfilTask(CheckReport report) {
        this.report = report;
    }

    @Override
    public String getName() {
        return "Fill report";
    }

    @Override
    public IdleTask.Context getContext() {
        return new IdleTask.Context();
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        context.setSharedContext(new ReportContext(report), ReportContext.class);
        run(context, report);
        return success();
    }

    protected abstract void run(PipelineContext context, CheckReport report);

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(CheckPipelineTask.class);
    }
}
