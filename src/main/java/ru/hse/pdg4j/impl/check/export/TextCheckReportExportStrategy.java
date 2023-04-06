package ru.hse.pdg4j.impl.check.export;

import ru.hse.pdg4j.api.check.CheckReport;
import ru.hse.pdg4j.api.check.CheckReportEntry;
import ru.hse.pdg4j.api.check.CheckReportExportStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TextCheckReportExportStrategy implements CheckReportExportStrategy<String> {
    @Override
    public String export(CheckReport report) {
        List<CheckReportEntry> entries = new ArrayList<>(report.getEntries());
        StringBuilder builder = new StringBuilder();
        builder.append("Total ")
                .append(entries.size())
                .append(" checks requested:")
                .append('\n')
                .append('\n');

        var byPerformed = entries.stream().collect(Collectors.groupingBy(CheckReportEntry::isPerformed));
        if (byPerformed.get(false) != null) {
            builder.append("Unperformed:").append('\n');
            for (CheckReportEntry checkReportEntry : byPerformed.get(false)) {
                writeCheckReportEntry(checkReportEntry, builder);
            }
            builder.append("\n");
        }

        var bySuccess = byPerformed.get(true).stream().collect(Collectors.groupingBy(CheckReportEntry::isSuccessful));
        if (bySuccess.get(false) != null) {
            builder.append("FAILED:").append('\n');
            for (CheckReportEntry checkReportEntry : bySuccess.get(false)) {
                writeCheckReportEntry(checkReportEntry, builder);
            }
            builder.append("\n");
        }

        if (bySuccess.get(true) != null) {
            builder.append("Succeeded:").append('\n');
            for (CheckReportEntry checkReportEntry : bySuccess.get(true)) {
                writeCheckReportEntry(checkReportEntry, builder);
            }
            builder.append("\n");
        }

        builder.append("Order: ")
                .append(entries.stream()
                        .map(CheckReportEntry::getName)
                        .collect(Collectors.joining(", ")));

        return builder.toString().stripTrailing();
    }

    private void writeCheckReportEntry(CheckReportEntry entry, StringBuilder builder) {
        builder.append(entry.getName());
        builder.append("\n");
        for (String warning : entry.getWarnings()) {
            builder.append("W: ").append(warning).append("\n");
        }
        for (String error : entry.getErrors()) {
            builder.append("ERR: ").append(error).append("\n");
        }
        builder.append("\n");
    }
}
