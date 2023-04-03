package ru.hse.pdg4j.api.check;

import java.util.Collection;

public interface CheckReport {
    void append(CheckReportEntry entry);

    Collection<CheckReportEntry> getEntries();

    default <V> V export(CheckReportExportStrategy<V> strategy) {
        return strategy.export(this);
    }
}
