package ru.hse.pdg4j.impl.user.log;

import ru.hse.pdg4j.api.ExecutionListener;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.task.NonContextualPipelineTask;
import ru.hse.pdg4j.impl.task.util.LoggingExecutionListener;
import ru.hse.pdg4j.impl.user.BootstrapContext;

import java.util.logging.Logger;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class LogPipelineBuilderTask extends NonContextualPipelineTask {
    public LogPipelineBuilderTask() {
        super("Log");
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        BootstrapContext bootstrapContext = context.getSharedContext(BootstrapContext.class);
        if (!bootstrapContext.getOptions().getLogOptions().isSilent()) {
            bootstrapContext.setListener(ExecutionListener.combine(
                    bootstrapContext.getListener(),
                    new LoggingExecutionListener(Logger.getLogger("Execution"))
            ));
        }
        return success();
    }
}
