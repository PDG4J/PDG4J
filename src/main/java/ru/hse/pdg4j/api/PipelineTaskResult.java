package ru.hse.pdg4j.api;

public interface PipelineTaskResult {
    boolean isSuccessful();
    String getMessage();
}
