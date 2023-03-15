package ru.hse.pdg4j.impl.task.basic;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class MethodExtractionTask implements PipelineTask<MethodExtractionTask.Context> {
    public record Context(List<CtMethod<?>> methods) implements PipelineTaskContext {
    }

    private Context context;

    @Override
    public String getName() {
        return "Extract methods";
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        List<CtMethod<?>> methods = new ArrayList<>();
        var launcherContext = context.getContext(LauncherTask.Context.class);
        for (CtType<?> ctType : launcherContext.launcher().getFactory().Class().getAll()) {
            methods.addAll(ctType.getMethods());
        }
        this.context = new Context(methods);
        return success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(LauncherTask.class);
    }
}
