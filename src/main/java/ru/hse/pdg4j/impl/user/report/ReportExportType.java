package ru.hse.pdg4j.impl.user.report;

import ru.hse.pdg4j.api.check.CheckReportExportStrategy;
import ru.hse.pdg4j.impl.check.export.JsonCheckReportExportStrategy;
import ru.hse.pdg4j.impl.check.export.TextCheckReportExportStrategy;

public enum ReportExportType {
    TEXT(new TextCheckReportExportStrategy()),
    JSON(new JsonCheckReportExportStrategy());

    final CheckReportExportStrategy<String> strategy;

    ReportExportType(CheckReportExportStrategy<String> strategy) {
        this.strategy = strategy;
    }

    public CheckReportExportStrategy<String> getStrategy() {
        return strategy;
    }
}
