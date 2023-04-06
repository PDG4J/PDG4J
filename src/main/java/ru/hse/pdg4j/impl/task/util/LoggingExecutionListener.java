package ru.hse.pdg4j.impl.task.util;

import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.slf4j.spi.LoggingEventBuilder;
import ru.hse.pdg4j.api.*;
import ru.hse.pdg4j.api.check.exception.CheckException;
import ru.hse.pdg4j.api.check.exception.ErrorCheckException;
import ru.hse.pdg4j.api.check.exception.PassCheckException;
import ru.hse.pdg4j.api.check.task.CheckPipelineTask;

public class LoggingExecutionListener implements ExecutionListener {
    private final Logger logger;

    public LoggingExecutionListener(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void onComplete(PipelineTask<?> pipelineTask, PipelineTaskResult result, PipelineGraphNode current, PipelineContext context) {
        logger.info("{}: {}",
                pipelineTask.getName(),
                result.isSuccessful()
                        ? "OK"
                        : "FAIL: " + result.getMessage());

        if (pipelineTask instanceof CheckPipelineTask checkPipelineTask) {
            for (String warning : checkPipelineTask.getContext().getWarnings()) {
                logger.warn("{}: {}", checkPipelineTask.getName(), warning);
            }
        }
    }

    @Override
    public void onException(PipelineTask<?> pipelineTask, PipelineGraphNode current, Exception e, PipelineContext context) {
        LoggingEventBuilder log = logger.atLevel(pipelineTask.isBlocking() ? Level.ERROR : Level.WARN);
        if (e instanceof CheckException) {
            if (e instanceof PassCheckException) {
                log.log("{} skipped", pipelineTask.getName());
            } else if (e instanceof ErrorCheckException) {
                log.log("{} failed", pipelineTask.getName());
            } else {
                log.log("{} unknown", pipelineTask.getName());
            }
            for (String error : ((CheckPipelineTask) pipelineTask).getContext().getErrors()) {
                logger.error("{}", error);
            }
            for (String warning : ((CheckPipelineTask) pipelineTask).getContext().getWarnings()) {
                logger.warn("{}", warning);
            }
        } else {
            log.log("Fail executing \"{}\": {}", pipelineTask.getName(), e.getMessage());
        }
    }

    @Override
    public void onFinish(PipelineContext context) {
        logger.info("Finished executing the pipeline");
    }
}
