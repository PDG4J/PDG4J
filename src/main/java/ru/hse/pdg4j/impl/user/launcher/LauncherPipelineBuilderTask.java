package ru.hse.pdg4j.impl.user.launcher;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.task.NonContextualPipelineTask;
import ru.hse.pdg4j.impl.task.basic.LauncherTask;
import ru.hse.pdg4j.impl.user.BootstrapContext;

import java.io.File;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class LauncherPipelineBuilderTask extends NonContextualPipelineTask {
    public LauncherPipelineBuilderTask() {
        super("Launcher");
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        BootstrapContext bootstrapContext = context.getSharedContext(BootstrapContext.class);
        LauncherOptions launcherOptions = bootstrapContext.getOptions().getLauncherOptions();
        bootstrapContext.getBuilder().task(
                new LauncherTask(
                        new File(launcherOptions.getSourcePath()),
                        launcherOptions.getClasspath() == null
                                ? null
                                : launcherOptions.getClasspath().split(";")
                ));
        return success();
    }
}
