package ru.hse.pdg4j.api.check.task;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.CheckReportEntry;
import ru.hse.pdg4j.api.check.exception.CheckException;
import ru.hse.pdg4j.api.check.exception.ErrorCheckException;
import ru.hse.pdg4j.api.check.exception.PassCheckException;

/**
 * Special task type suited for checks
 */
public abstract class CheckPipelineTask implements PipelineTask<CheckReportEntry> {
    private final String name;
    private final CheckReportEntry entry;

    public CheckPipelineTask(String name) {
        this.name = name;
        this.entry = new CheckReportEntry(name);
    }

    @Override
    public CheckReportEntry getContext() {
        return entry;
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        try {
            perform(context);
            entry.setPerformed(true);
            return new BasicPipelineTaskResult(entry.isSuccessful());
        } catch (PassCheckException e) {
            return new BasicPipelineTaskResult(false, "pass");
        } catch (CheckException e) {
            entry.setPerformed(true);
            return new BasicPipelineTaskResult(false, "check failed");
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicPipelineTaskResult(
                    false,
                    "Internal error performing: failed to perform (" + e.getMessage() + ")");
        }
    }

    /**
     * Perform the check with pipeline context
     *
     * @param context to perform the check with
     */
    public abstract void perform(PipelineContext context);

    protected void pass(String error) {
        entry.getErrors().add(error);
        throw new PassCheckException();
    }

    protected void pass() {
        throw new PassCheckException();
    }

    protected void warning(String warning) {
        entry.getWarnings().add(warning);
    }

    protected void error(String error) {
        entry.getErrors().add(error);
        throw new ErrorCheckException();
    }

    protected void errorAndContinue(String error) {
        entry.getErrors().add(error);
    }

    protected void success() {
        entry.setSuccessful(true);
    }

    /**
     * By default, blocking checks do not fail the entire pipeline but all the other checks instead
     *
     * @return if the check is blocking
     */
    @Override
    public boolean isBlocking() {
        return true;
    }

    @Override
    public String getName() {
        return "Check: %s".formatted(name);
    }

    /**
     * Basic class for the task result encapsulating only the fact of task's success and the message
     */
    private static class BasicPipelineTaskResult implements PipelineTaskResult {
        private final boolean success;
        private final String message;

        public BasicPipelineTaskResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public BasicPipelineTaskResult(boolean success) {
            this.success = success;
            this.message = null;
        }

        @Override
        public boolean isSuccessful() {
            return success;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
