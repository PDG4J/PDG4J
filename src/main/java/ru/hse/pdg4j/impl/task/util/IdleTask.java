package ru.hse.pdg4j.impl.task.util;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.api.PipelineTaskResult;

import java.util.Collection;
import java.util.Collections;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class IdleTask implements PipelineTask<IdleTask.Context> {
    @Override
    public String getName() {
        return "idle";
    }

    @Override
    public Context getContext() {
        return new Context();
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        return success();
    }

    public static class Context implements PipelineTaskContext {}

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return Collections.emptyList();
    }
}
