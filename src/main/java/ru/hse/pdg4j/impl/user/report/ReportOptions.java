package ru.hse.pdg4j.impl.user.report;

import picocli.CommandLine;

public class ReportOptions {
    @CommandLine.ArgGroup(exclusive = false)
    private Report report;

    public Report getReport() {
        return report;
    }

    @Override
    public String toString() {
        return "ReportOptions{" +
                "report=" + report +
                '}';
    }

    static class Report {
        @CommandLine.Option(names = "-r", fallbackValue = "true", description = "Whether to output a report or not", required = true)
        private boolean writeReport;

        @CommandLine.Option(names = "-rt", defaultValue = "TEXT", description = "Format in which the report is printed", required = true)
        private ReportExportType exportType;

        @CommandLine.Option(names = "-rf", defaultValue = "report",
                description = "Specify the file in which the report is written." +
                        " If not specified, the output is printed to stdout instead",
                required = true)
        private String reportFile;

        public boolean isWriteReport() {
            return writeReport;
        }

        public ReportExportType getExportType() {
            return exportType;
        }

        public String getReportFile() {
            return reportFile;
        }

        @Override
        public String toString() {
            return "Report{" +
                    "writeReport=" + writeReport +
                    ", exportType=" + exportType +
                    ", reportFile='" + reportFile + '\'' +
                    '}';
        }
    }
}
