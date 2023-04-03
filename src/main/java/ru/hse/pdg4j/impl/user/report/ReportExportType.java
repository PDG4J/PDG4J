package ru.hse.pdg4j.impl.user.report;

import ru.hse.pdg4j.api.check.CheckReportExportStrategy;
import ru.hse.pdg4j.impl.check.export.JsonCheckReportExportStrategy;
import ru.hse.pdg4j.impl.check.export.TextCheckReportExportStrategy;

public enum ReportExportType {
    TEXT(new TextCheckReportExportStrategy()),
    JSON(new JsonCheckReportExportStrategy());

    ReportExportType(CheckReportExportStrategy<String> strategy) {
        this.strategy = strategy;
    }

    final CheckReportExportStrategy<String> strategy;

    public CheckReportExportStrategy<String> getStrategy() {
        return strategy;
    }
}
