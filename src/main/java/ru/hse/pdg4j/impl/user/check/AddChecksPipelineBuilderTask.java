package ru.hse.pdg4j.impl.user.check;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.task.CheckPipelineTask;
import ru.hse.pdg4j.api.check.task.NonContextualPipelineTask;
import ru.hse.pdg4j.api.user.BootstrapContext;
import ru.hse.pdg4j.impl.builder.PipelineGraphBuilder;
import ru.hse.pdg4j.impl.check.builtin.DuplicateCodeCheckTask;
import ru.hse.pdg4j.impl.check.builtin.DuplicateCodeInFunctionTask;
import ru.hse.pdg4j.impl.task.basic.SourceInitialCheckTask;
import ru.hse.pdg4j.impl.task.util.IdleCheckTask;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class AddChecksPipelineBuilderTask extends NonContextualPipelineTask {
    public AddChecksPipelineBuilderTask() {
        super("Add");
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        BootstrapContext bootstrapContext = context.getSharedContext(BootstrapContext.class);
        PipelineGraphBuilder builder = bootstrapContext.getAnalysisGraphBuilder();

        // Check for initial source code: compliance with Java 11, syntax errors, etc.
        builder.task(new SourceInitialCheckTask());

        // Builtin checks for duplicate code
        builder.task(new DuplicateCodeCheckTask())
                .task(new DuplicateCodeInFunctionTask());

        return success();
    }
}
