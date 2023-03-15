package ru.hse.pdg4j.impl;

import ru.hse.pdg4j.api.PipelineTaskResult;

public class SimplePipelineTaskResult {
    public static PipelineTaskResult success(String message) {
        return new PipelineTaskResult() {
            @Override
            public boolean isSuccessful() {
                return true;
            }

            @Override
            public String getMessage() {
                return message;
            }
        };
    }

    public static PipelineTaskResult success() {
        return success(null);
    }

    public static PipelineTaskResult failure(String message) {
        return new PipelineTaskResult() {
            @Override
            public boolean isSuccessful() {
                return false;
            }

            @Override
            public String getMessage() {
                return message;
            }
        };
    }
}
