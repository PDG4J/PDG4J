package ru.hse.pdg4j.api.check.task;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.api.check.CheckReportEntry;
import ru.hse.pdg4j.api.check.exception.CheckException;
import ru.hse.pdg4j.api.check.exception.ErrorCheckException;
import ru.hse.pdg4j.api.check.exception.PassCheckException;

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
            return new PipelineTaskResult() {
                @Override
                public boolean isSuccessful() {
                    return entry.isSuccessful();
                }

                @Override
                public String getMessage() {
                    return null;
                }
            };
        } catch (PassCheckException e) {
            return new PipelineTaskResult() {
                @Override
                public boolean isSuccessful() {
                    return false;
                }

                @Override
                public String getMessage() {
                    return "pass";
                }
            };
        } catch (CheckException e) {
            entry.setPerformed(true);
            return new PipelineTaskResult() {
                @Override
                public boolean isSuccessful() {
                    return false;
                }

                @Override
                public String getMessage() {
                    return "check failed";
                }
            };
        } catch (Exception e) {
            return new PipelineTaskResult() {
                @Override
                public boolean isSuccessful() {
                    return false;
                }

                @Override
                public String getMessage() {
                    return "Internal error performing the check: failed to perform";
                }
            };
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

    @Override
    public boolean isBlocking() {
        return true;
    }

    @Override
    public String getName() {
        return "Check: %s".formatted(name);
    }
}
