package ru.hse.pdg4j.impl.check;

import ru.hse.pdg4j.api.check.CheckReport;
import ru.hse.pdg4j.api.check.CheckReportEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SequentialCheckReport implements CheckReport {
    private final List<CheckReportEntry> entryList;

    public SequentialCheckReport() {
        this.entryList = new ArrayList<>();
    }

    @Override
    public void append(CheckReportEntry entry) {
        entryList.add(entry);
    }

    @Override
    public List<CheckReportEntry> getEntries() {
        return entryList;
    }
}
