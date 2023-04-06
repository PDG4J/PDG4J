package ru.hse.pdg4j.impl.task.report;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.check.CheckReport;
import ru.hse.pdg4j.api.check.task.CheckPipelineTask;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ReportFulfilFilterTask extends ReportFulfilTask {
    private final List<Predicate<String>> skipFilters;

    public ReportFulfilFilterTask(CheckReport report, String[] ignoredPatterns) {
        super(report);
        this.skipFilters = Arrays.stream(ignoredPatterns)
                .map(Pattern::compile)
                .map(Pattern::asPredicate)
                .collect(Collectors.toList());
    }

    public ReportFulfilFilterTask(CheckReport report, List<Predicate<String>> skipFilters) {
        super(report);
        this.skipFilters = skipFilters;
    }

    @Override
    protected void run(PipelineContext context, CheckReport report) {
        for (PipelineTask<?> task : context.getExecuted()) {
            if (task instanceof CheckPipelineTask checkTask
                    && skipFilters.stream().noneMatch(it -> it.test(task.getName()))) {
                report.append(checkTask.getContext());
            }
        }
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return Collections.singleton(CheckPipelineTask.class);
    }
}
