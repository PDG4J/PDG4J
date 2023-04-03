package ru.hse.pdg4j.api.check.fill;

import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;

public abstract class CheckReportFillTask implements PipelineTask<CheckReportFillTask.Context> {
    public record Context() implements PipelineTaskContext {
    }
}
