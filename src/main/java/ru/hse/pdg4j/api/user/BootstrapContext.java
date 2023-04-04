package ru.hse.pdg4j.api.user;

import picocli.CommandLine;
import ru.hse.pdg4j.api.ExecutionListener;
import ru.hse.pdg4j.api.PipelineSharedContext;
import ru.hse.pdg4j.impl.builder.PipelineGraphBuilder;
import ru.hse.pdg4j.impl.task.util.IdleExecutionListener;
import ru.hse.pdg4j.impl.user.CommandOptions;

/**
 * Context for the original program launch containing analysis graph builder, command options and a custom listener
 */
public class BootstrapContext implements PipelineSharedContext {
    private final PipelineGraphBuilder analysisGraphBuilder;
    private final CommandOptions options;
    private final CommandLine commandLine;
    private ExecutionListener analysisExecutionListener;

    public BootstrapContext(CommandOptions options, CommandLine commandLine) {
        this.analysisGraphBuilder = new PipelineGraphBuilder();
        this.analysisExecutionListener = new IdleExecutionListener();
        this.options = options;
        this.commandLine = commandLine;
    }

    /**
     * Get graph builder for the future analysis pipeline
     *
     * @return analysis pipeline graph builder
     */
    public PipelineGraphBuilder getAnalysisGraphBuilder() {
        return analysisGraphBuilder;
    }

    /**
     * Get execution listener for the future analysis pipeline
     *
     * @return analysis execution listener
     */
    public ExecutionListener getAnalysisExecutionListener() {
        return analysisExecutionListener;
    }

    /**
     * Set execution listener for the future analysis pipeline.
     *
     * @param analysisExecutionListener listener to set for the future analysis pipeline
     * @apiNote It is highly recommended that the user of the API combines listeners
     * @see ExecutionListener#combine(ExecutionListener...)
     */
    public void setAnalysisExecutionListener(ExecutionListener analysisExecutionListener) {
        this.analysisExecutionListener = analysisExecutionListener;
    }

    /**
     * Get standard PDG4J command options retrieved from the user
     *
     * @return command options
     */
    public CommandOptions getOptions() {
        return options;
    }

    /**
     * Get command line containing all the parsed args, mixins etc.
     *
     * @return command line
     * @see CommandLine
     */
    public CommandLine getCommandLine() {
        return commandLine;
    }
}
