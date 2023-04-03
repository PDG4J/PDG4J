package ru.hse.pdg4j.impl;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineSharedContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BasePipelineContext implements PipelineContext {
    private final Map<Class<? extends PipelineTaskContext>, PipelineTaskContext> contextMap;
    private final Map<Class<? extends PipelineSharedContext>, PipelineSharedContext> sharedContextMap;
    @SuppressWarnings("rawtypes")
    private final Map<Class<? extends PipelineTask>, PipelineTask<?>> taskMap;

    public BasePipelineContext() {
        contextMap = new HashMap<>();
        sharedContextMap = new ConcurrentHashMap<>();
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
    public <T extends PipelineSharedContext> T getSharedContext(Class<T> clazz) {
        return (T) sharedContextMap.get(clazz);
    }

    @Override
    public <T extends PipelineSharedContext> void setSharedContext(T context, Class<T> clazz) {
        sharedContextMap.put(clazz, context);
    }

    @Override
    public <T extends PipelineTask<?>> T getTask(Class<T> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers())) {
            // Search through existing classes for implementations
            for (Class<? extends PipelineTask> keyClass : taskMap.keySet()) {
                if (clazz.isAssignableFrom(keyClass)) {
                    // Child class is always convertible to its abstract parent
                    //noinspection unchecked
                    return (T) taskMap.get(keyClass);
                }
            }
        }
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
