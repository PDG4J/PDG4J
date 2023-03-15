package ru.hse.pdg4j.impl;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class BasePipelineContext implements PipelineContext {
    private final Map<Class<? extends PipelineTaskContext>, PipelineTaskContext> contextMap;
    @SuppressWarnings("rawtypes")
    private final Map<Class<? extends PipelineTask>, PipelineTask<?>> taskMap;

    public BasePipelineContext() {
        contextMap = new HashMap<>();
        taskMap = new HashMap<>();
    }

    @Override
    public <T extends PipelineTaskContext> T getContext(Class<T> clazz) {
        Object result = contextMap.get(clazz);
        if (result == null) {
            return null;
        }
        //noinspection unchecked
        return (T) result;
    }

    @Override
    public <T extends PipelineTask<?>> T getTask(Class<T> clazz) {
        Object result = taskMap.get(clazz);
        if (result == null) {
            return null;
        }
        //noinspection unchecked
        return (T) result;
    }

    <T extends PipelineTaskContext> void put(PipelineTask<T> task) {
        contextMap.put(task.getContext().getClass(), task.getContext());
        taskMap.put(task.getClass(), task);
    }
}
