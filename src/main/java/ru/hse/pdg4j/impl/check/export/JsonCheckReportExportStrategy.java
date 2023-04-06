package ru.hse.pdg4j.impl.check.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.hse.pdg4j.api.check.CheckReport;
import ru.hse.pdg4j.api.check.CheckReportEntry;
import ru.hse.pdg4j.api.check.CheckReportExportStrategy;

import java.util.*;
import java.util.stream.Collectors;

public class JsonCheckReportExportStrategy implements CheckReportExportStrategy<String> {
    private final Gson gson;

    public JsonCheckReportExportStrategy(Gson gson) {
        this.gson = gson;
    }

    public JsonCheckReportExportStrategy() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public String export(CheckReport report) {
        Map<String, Object> object = new HashMap<>();

        List<String> order = new ArrayList<>();
        for (CheckReportEntry entry : report.getEntries()) {
            order.add(entry.getName());
        }
        object.put("order", order);

        var byPerformed = report.getEntries().stream().collect(Collectors.groupingBy(CheckReportEntry::isPerformed));
        object.put("unperformed", byPerformed.get(false));

        var bySuccess = Optional.ofNullable(byPerformed.get(true))
                .orElse(Collections.emptyList())
                .stream().collect(Collectors.groupingBy(CheckReportEntry::isSuccessful));

        object.put("success", bySuccess.get(true));
        object.put("fail", bySuccess.get(false));

        return gson.toJson(object);
    }
}
