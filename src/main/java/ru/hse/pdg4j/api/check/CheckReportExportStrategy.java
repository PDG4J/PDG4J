package ru.hse.pdg4j.api.check;

public interface CheckReportExportStrategy<V> {
    /**
     * Export the report
     *
     * @param report to export
     * @return result object representing the exported report
     */
    V export(CheckReport report);
}
