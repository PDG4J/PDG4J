package ru.hse.pdg4j.impl.user;

import ru.hse.pdg4j.api.ExecutionListener;
import ru.hse.pdg4j.api.PipelineSharedContext;
import ru.hse.pdg4j.impl.builder.PipelineGraphBuilder;
import ru.hse.pdg4j.impl.task.util.IdleExecutionListener;

public class BootstrapContext implements PipelineSharedContext {
    private final PipelineGraphBuilder builder;
    private ExecutionListener listener;
    private final CommandOptions options;

    public BootstrapContext(CommandOptions options) {
        this.builder = new PipelineGraphBuilder();
        this.listener = new IdleExecutionListener();
        this.options = options;
    }

    public PipelineGraphBuilder getBuilder() {
        return builder;
    }

    public ExecutionListener getListener() {
        return listener;
    }

    public void setListener(ExecutionListener listener) {
        this.listener = listener;
    }

    public CommandOptions getOptions() {
        return options;
    }
}
