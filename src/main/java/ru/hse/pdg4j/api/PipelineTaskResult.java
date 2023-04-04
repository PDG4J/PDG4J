package ru.hse.pdg4j.api;

public interface PipelineTaskResult {
    /**
     * Whether the task was performed successfuly
     *
     * @return is task successful
     */
    boolean isSuccessful();

    /**
     * Get a message of the result. Could be assumed to be null if the result is successful.
     *
     * @return message
     */
    String getMessage();
}
