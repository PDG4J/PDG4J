package ru.hse.pdg4j.impl.task.basic;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.Environment;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.failure;
import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class LauncherTask implements PipelineTask<LauncherTask.Context> {
    record Context(Launcher launcher, SpoonModelBuilder compiler) implements PipelineTaskContext {
    }

    private final File sourceFolder;
    private final String[] classpath;

    public LauncherTask(File sourceFolder, String[] classpath) {
        this.sourceFolder = sourceFolder;
        this.classpath = classpath;
    }

    private Context context;

    @Override
    public String getName() {
        return "Initial source inspection";
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

        launcher.run();
        SpoonModelBuilder compiler = launcher.createCompiler();
        if (!compiler.compile(SpoonModelBuilder.InputType.FILES)) {
            return failure("Compilation failed");
        }

        this.context = new Context(launcher, compiler);
        return success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return Collections.emptyList();
    }
}
