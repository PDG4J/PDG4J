package ru.hse.pdg4j.api.check.task;

import ru.hse.pdg4j.api.PipelineTask;

public abstract class NonContextualPipelineTask implements PipelineTask<EmptyContext> {
    private final String name;

    public NonContextualPipelineTask(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public EmptyContext getContext() {
        return new EmptyContext();
    }
}
