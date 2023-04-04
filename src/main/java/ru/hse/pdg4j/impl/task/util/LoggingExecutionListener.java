package ru.hse.pdg4j.impl.task.util;

import ru.hse.pdg4j.api.*;

import java.util.logging.Logger;

public class LoggingExecutionListener implements ExecutionListener {
    private final Logger logger;

    public LoggingExecutionListener(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void onComplete(PipelineTask<?> pipelineTask, PipelineTaskResult result, PipelineGraphNode current, PipelineContext context) {
        logger.info(String.format("%s: %s%n",
                pipelineTask.getName(),
                result.isSuccessful()
                        ? "OK"
                        : "FAIL: " + result.getMessage()));
    }

    @Override
    public void onException(PipelineTask<?> pipelineTask, PipelineGraphNode current, Exception e, PipelineContext context) {
        logger.warning("Error executing " + pipelineTask.getName());
        e.printStackTrace();
    }

    @Override
    public void onFinish(PipelineContext context) {
        logger.info("Finished executing the pipeline");
    }
}
