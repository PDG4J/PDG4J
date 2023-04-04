package ru.hse.pdg4j.impl.user.entry;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.task.NonContextualPipelineTask;
import ru.hse.pdg4j.impl.user.BootstrapContext;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class EntryPointPipelineBuilderTask extends NonContextualPipelineTask {
    private final BootstrapContext context;

    public EntryPointPipelineBuilderTask(BootstrapContext context) {
        super("Entry");
        this.context = context;
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        context.setSharedContext(this.context, BootstrapContext.class);
        return success();
    }
}
