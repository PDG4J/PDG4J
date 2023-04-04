package ru.hse.pdg4j.api.plugin;

import org.pf4j.ExtensionPoint;
import ru.hse.pdg4j.api.user.BootstrapContext;
import ru.hse.pdg4j.impl.builder.PipelineGraphBuilder;

/**
 * Extension interface for all the plugins for PDG4J
 */
public interface Pdg4jExtension extends ExtensionPoint {
    /**
     * Get name of the extension
     *
     * @return name
     */
    String getName();

    /**
     * Make extensions for the setup pipeline.
     * Setup pipeline is the pipeline made and executed to form the main analysis pipeline (to fill it with tasks)
     * given the bootstrap context.
     *
     * @param setupGraphBuilder graph builder for the future setup pipeline
     * @param context           context with essential input parameters
     */
    default void extendSetupPipeline(PipelineGraphBuilder setupGraphBuilder, BootstrapContext context) {
    }

    /**
     * Make extensions for the main analysis pipeline.
     *
     * @param analysisGraphBuilder graph builder for the future analysis pipeline
     * @param context              context with essential input parameters
     */
    default void extendAnalysisPipeline(PipelineGraphBuilder analysisGraphBuilder, BootstrapContext context) {
    }

    /**
     * Get new custom command options for the analyzer cli.
     * Returned object will be used as a mixin of <a href="https://picocli.info/">piocli</a> options alongside
     * with the main options.
     *
     * @return custom piocli options object
     * @apiNote if the null is returned, custom cli options are ignored
     * @see ru.hse.pdg4j.impl.user.CommandOptions
     * @see Pdg4jExtension#extractCustomOptions(BootstrapContext)
     */
    default Object getCustomOptions() {
        return null;
    }

    /**
     * Helper method to extract custom options from the bootstrap context.
     * The extracted mixin is expected to be of the same type as the specified custom options object
     * and the specified type parameter for this method
     *
     * @param context bootstrap context
     * @param <T>     of the custom options to extract
     * @return filled the custom options object
     * @see Pdg4jExtension#getCustomOptions()
     */
    default <T> T extractCustomOptions(BootstrapContext context) {
        // The mixin is expected to be of an applicable type
        //noinspection unchecked
        return (T) context.getCommandLine().getMixins().get(getName());
    }
}
