package ru.hse.pdg4j.impl.user.check;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.task.NonContextualPipelineTask;
import ru.hse.pdg4j.impl.builder.PipelineGraphBuilder;
import ru.hse.pdg4j.impl.task.util.IdleCheckTask;
import ru.hse.pdg4j.impl.user.BootstrapContext;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class AddChecksPipelineBuilderTask extends NonContextualPipelineTask {
    public AddChecksPipelineBuilderTask() {
        super("Add");
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        // TODO: maybe add checks' auto-recognition via annotations similar to Spring's approach
        BootstrapContext bootstrapContext = context.getSharedContext(BootstrapContext.class);
        PipelineGraphBuilder builder = bootstrapContext.getBuilder();
        builder.task(new IdleCheckTask("wohooo1").setSuccess(true).setBlocking(false));
        builder.task(new IdleCheckTask("blocker").setSuccess(false).setBlocking(true), "Check: wohooo1");
        builder.task(new IdleCheckTask("wohooo2").setSuccess(true).setBlocking(false), "Check: blocker");

        return success();
    }
}