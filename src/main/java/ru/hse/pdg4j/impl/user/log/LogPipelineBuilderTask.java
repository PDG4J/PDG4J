package ru.hse.pdg4j.impl.user.log;

import ch.qos.logback.classic.Level;
import org.slf4j.LoggerFactory;
import ru.hse.pdg4j.api.ExecutionListener;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.task.NonContextualPipelineTask;
import ru.hse.pdg4j.api.user.BootstrapContext;
import ru.hse.pdg4j.impl.task.util.LoggingExecutionListener;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class LogPipelineBuilderTask extends NonContextualPipelineTask {
    public LogPipelineBuilderTask() {
        super("Log");
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        BootstrapContext bootstrapContext = context.getSharedContext(BootstrapContext.class);
        if (!bootstrapContext.getOptions().getLogOptions().isSilent()) {
            // Change root logger's level to INFO
            ch.qos.logback.classic.Logger root =
                    (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
            root.setLevel(Level.INFO);
            bootstrapContext.setAnalysisExecutionListener(ExecutionListener.combine(
                    bootstrapContext.getAnalysisExecutionListener(),
                    new LoggingExecutionListener(LoggerFactory.getLogger("Execution"))
            ));
        }
        return success();
    }
}
