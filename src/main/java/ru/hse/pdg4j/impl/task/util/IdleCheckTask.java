package ru.hse.pdg4j.impl.task.util;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.check.task.CheckPipelineTask;

public class IdleCheckTask extends CheckPipelineTask {
    private boolean blocking = false;
    private boolean success = true;

    public IdleCheckTask(String name) {
        super(name);
    }

    public IdleCheckTask setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    @Override
    public void perform(PipelineContext context) {
        if (!success) {
            error("Sample failure message");
        } else {
            success();
        }
    }

    @Override
    public boolean isBlocking() {
        return blocking;
    }

    public IdleCheckTask setBlocking(boolean blocking) {
        this.blocking = blocking;
        return this;
    }
}
