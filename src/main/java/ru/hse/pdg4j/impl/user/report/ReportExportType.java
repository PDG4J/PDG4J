package ru.hse.pdg4j.impl.user.report;

import ru.hse.pdg4j.api.check.CheckReportExportStrategy;
import ru.hse.pdg4j.impl.check.export.JsonCheckReportExportStrategy;
import ru.hse.pdg4j.impl.check.export.TextCheckReportExportStrategy;

public enum ReportExportType {
    TEXT(new TextCheckReportExportStrategy(), "txt"),
    JSON(new JsonCheckReportExportStrategy(), "json");

    final CheckReportExportStrategy<String> strategy;
    final String extension;

    ReportExportType(CheckReportExportStrategy<String> strategy, String extension) {
        this.strategy = strategy;
        this.extension = extension;
    }

    public CheckReportExportStrategy<String> getStrategy() {
        return strategy;
    }

    public String getExtension() {
        return extension;
    }
}
