package ru.hse.pdg4j.api.check;

public interface CheckReportExportStrategy<V> {
    V export(CheckReport report);
}
