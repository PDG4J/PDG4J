package ru.hse.pdg4j.impl.task.report;

import ru.hse.pdg4j.api.PipelineSharedContext;
import ru.hse.pdg4j.api.check.CheckReport;

public class ReportContext implements PipelineSharedContext {
    private final CheckReport checkReport;

    public ReportContext(CheckReport checkReport) {
        this.checkReport = checkReport;
    }

    public CheckReport getCheckReport() {
        return checkReport;
    }
}
