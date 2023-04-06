package ru.hse.pdg4j.impl.user.launcher;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.task.NonContextualPipelineTask;
import ru.hse.pdg4j.api.user.BootstrapContext;
import ru.hse.pdg4j.impl.task.basic.LauncherTask;

import java.io.File;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.failure;
import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class LauncherPipelineBuilderTask extends NonContextualPipelineTask {
    public LauncherPipelineBuilderTask() {
        super("Launcher");
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        BootstrapContext bootstrapContext = context.getSharedContext(BootstrapContext.class);
        LauncherOptions launcherOptions = bootstrapContext.getOptions().getLauncherOptions();
        File sourceFolder = new File(launcherOptions.getSourcePath());
        if (!sourceFolder.exists()) {
            return failure("Provided source path does not exist");
        }
        if (!sourceFolder.isDirectory()) {
            return failure("Provided source path must resolve to a directory inside which the code is located");
        }

        bootstrapContext.getAnalysisGraphBuilder()
                .task(new LauncherTask(
                        sourceFolder,
                        launcherOptions.getClasspath() == null
                                ? null
                                : launcherOptions.getClasspath().split(";")
                ));
        return success();
    }
}
