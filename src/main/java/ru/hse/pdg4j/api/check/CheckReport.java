package ru.hse.pdg4j.api.check;

import java.util.Collection;

public interface CheckReport {
    /**
     * Append an entry to the report
     *
     * @param entry to append
     */
    void append(CheckReportEntry entry);

    /**
     * Get all entries there are
     *
     * @return entry collection
     */
    Collection<CheckReportEntry> getEntries();

    /**
     * Export the report using specified strategy
     *
     * @param strategy to use to export
     * @param <V>      type of result object
     * @return result object representing the exported report
     */
    default <V> V export(CheckReportExportStrategy<V> strategy) {
        return strategy.export(this);
    }
}
