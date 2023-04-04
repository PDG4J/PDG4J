package ru.hse.pdg4j.impl.task.basic;

import org.apache.commons.io.FileUtils;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.check.task.CheckPipelineTask;
import spoon.SpoonModelBuilder;
import spoon.support.compiler.SnippetCompilationError;

import java.util.Collection;
import java.util.List;

public class SourceInitialCheckTask extends CheckPipelineTask {
    public SourceInitialCheckTask() {
        super("Primary source code check");
    }

    @Override
    public void perform(PipelineContext context) {
        var launcherContext = context.getContext(LauncherTask.Context.class);
        try {
            launcherContext.launcher().run();
            if (!launcherContext.compiler().compile(SpoonModelBuilder.InputType.FILES)) {
                error("Compilation failed");
            }
        } catch (SnippetCompilationError e) {
            for (String problem : e.problems) {
                errorAndContinue(problem);
            }
            error("Compilation failed");
        } finally {
            try {
                FileUtils.deleteDirectory(launcherContext.compiler().getSourceOutputDirectory());
                FileUtils.deleteDirectory(launcherContext.compiler().getBinaryOutputDirectory());
            } catch (Exception e) {
                warning("Failed to clean up spoon dirs: " + e.getMessage());
            }
        }
        success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(LauncherTask.class);
    }
}
