package ru.hse.pdg4j.impl.user.check;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.task.CheckPipelineTask;
import ru.hse.pdg4j.api.check.task.NonContextualPipelineTask;
import ru.hse.pdg4j.api.user.BootstrapContext;
import ru.hse.pdg4j.impl.builder.PipelineGraphBuilder;
import ru.hse.pdg4j.impl.task.basic.SourceInitialCheckTask;
import ru.hse.pdg4j.impl.task.util.IdleCheckTask;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class AddChecksPipelineBuilderTask extends NonContextualPipelineTask {
    public AddChecksPipelineBuilderTask() {
        super("Add");
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        // TODO: maybe add checks' auto-recognition via annotations similar to Spring's approach
        BootstrapContext bootstrapContext = context.getSharedContext(BootstrapContext.class);
        PipelineGraphBuilder builder = bootstrapContext.getAnalysisGraphBuilder();
        builder.task(new SourceInitialCheckTask());
        builder.task(new IdleCheckTask("<Successful Check>").setSuccess(true).setBlocking(false));
        builder.task(new IdleCheckTask("<Failed Check>").setSuccess(false).setBlocking(false));
        builder.task(new CheckPipelineTask("<Skipped Check>") {
            @Override
            public void perform(PipelineContext context) {
                pass("Sample skip message");
            }

            @Override
            public boolean isBlocking() {
                return false;
            }
        });

        return success();
    }
}
