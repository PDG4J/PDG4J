package ru.hse.pdg4j.api.check;

import ru.hse.pdg4j.api.PipelineTaskContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CheckReportEntry implements PipelineTaskContext {
    private String name;
    private boolean isPerformed;
    private Boolean isSuccessful;
    private List<String> warnings;
    private List<String> errors;

    public CheckReportEntry(String name) {
        this.name = name;
        this.isPerformed = false;
        this.isSuccessful = false;
        this.warnings = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPerformed() {
        return isPerformed;
    }

    public void setPerformed(boolean performed) {
        isPerformed = performed;
    }

    public Boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(Boolean successful) {
        isSuccessful = successful;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckReportEntry that = (CheckReportEntry) o;
        return isPerformed == that.isPerformed &&
                Objects.equals(name, that.name) &&
                Objects.equals(isSuccessful, that.isSuccessful) &&
                Objects.equals(warnings, that.warnings) &&
                Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isPerformed, isSuccessful, warnings, errors);
    }
}
