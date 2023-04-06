package ru.hse.pdg4j.impl.task.basic;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.Environment;

import java.io.File;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class LauncherTask implements PipelineTask<LauncherTask.Context> {
    private final File sourceFolder;
    private final String[] classpath;
    private Context context;

    public LauncherTask(File sourceFolder, String[] classpath) {
        this.sourceFolder = sourceFolder;
        this.classpath = classpath;
    }

    @Override
    public String getName() {
        return "Spoon launcher init";
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(sourceFolder.getAbsolutePath());

        Environment environment = launcher.getEnvironment();
        environment.setComplianceLevel(11);
        environment.setShouldCompile(true);
        if (classpath != null) {
            environment.setSourceClasspath(classpath);
        }

        this.context = new Context(launcher, launcher.getModelBuilder());
        return success();
    }

    record Context(Launcher launcher, SpoonModelBuilder compiler) implements PipelineTaskContext {
    }
}
