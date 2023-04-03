package ru.hse.pdg4j.impl.user;

import picocli.CommandLine;
import ru.hse.pdg4j.impl.user.export.ExportOptions;
import ru.hse.pdg4j.impl.user.launcher.LauncherOptions;
import ru.hse.pdg4j.impl.user.log.LogOptions;
import ru.hse.pdg4j.impl.user.report.ReportOptions;

public class CommandOptions {
    @CommandLine.Mixin
    private LauncherOptions launcherOptions;
    @CommandLine.Mixin
    private ReportOptions reportOptions;
    @CommandLine.Mixin
    private LogOptions logOptions;
    @CommandLine.Mixin
    private ExportOptions exportOptions;

    public LauncherOptions getLauncherOptions() {
        return launcherOptions;
    }

    public ReportOptions getReportOptions() {
        return reportOptions;
    }

    public LogOptions getLogOptions() {
        return logOptions;
    }

    public ExportOptions getExportOptions() {
        return exportOptions;
    }

    @Override
    public String toString() {
        return "CommandOptions{" +
                "launcherOptions=" + launcherOptions +
                ", reportOptions=" + reportOptions +
                ", logOptions=" + logOptions +
                ", exportOptions=" + exportOptions +
                '}';
    }
}
