package ru.hse.pdg4j;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import ru.hse.pdg4j.api.*;
import ru.hse.pdg4j.api.plugin.Pdg4jExtension;
import ru.hse.pdg4j.api.user.BootstrapContext;
import ru.hse.pdg4j.impl.SimpleFlowPipeline;
import ru.hse.pdg4j.impl.builder.PipelineGraphBuilder;
import ru.hse.pdg4j.impl.user.CommandOptions;
import ru.hse.pdg4j.impl.user.check.AddChecksPipelineBuilderTask;
import ru.hse.pdg4j.impl.user.check.MakeStructuresPipelineBuilderTask;
import ru.hse.pdg4j.impl.user.entry.EntryPointPipelineBuilderTask;
import ru.hse.pdg4j.impl.user.export.ExportPipelineBuilderTask;
import ru.hse.pdg4j.impl.user.launcher.LauncherPipelineBuilderTask;
import ru.hse.pdg4j.impl.user.log.LogPipelineBuilderTask;
import ru.hse.pdg4j.impl.user.report.ReportPipelineBuilderTask;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
    // Built-in listener to fail the process instantly on any errors
    // used only in the pipeline that sets up all the parameters
    // for the main code analysis pipeline
    private static final ExecutionListener DIE_ON_FAILURE_LISTENER = new ExecutionListener() {
        @Override
        public void onComplete(PipelineTask<?> pipelineTask,
                               PipelineTaskResult result,
                               PipelineGraphNode current,
                               PipelineContext context) {
            if (!result.isSuccessful()) {
                System.err.println("Failed to setup the analysis: " + result.getMessage());
                System.exit(-1);
            }
        }

        @Override
        public void onException(PipelineTask<?> pipelineTask, PipelineGraphNode current, Exception e, PipelineContext context) {
            System.err.println("Failed to setup the analysis due to an error: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    };

    public static void main(String[] args) {
        PluginManager pluginManager = new DefaultPluginManager();
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        List<Pdg4jExtension> extensions = pluginManager.getExtensions(Pdg4jExtension.class);

        CommandOptions commandOptions = new CommandOptions();
        CommandLine commandLine = new CommandLine(commandOptions);
        // Adding all the custom options
        for (Pdg4jExtension extension : extensions) {
            Object options = extension.getCustomOptions();
            if (options != null) {
                commandLine.addMixin(extension.getName(), options);
            }
        }
        // Parsing prompted args
        try {
            commandLine.parseArgs(args);
            if (commandLine.isUsageHelpRequested()) {
                commandLine.usage(System.out);
                return;
            }
        } catch (CommandLine.ParameterException e) {
            System.err.println("Invalid usage: " + e.getMessage());
            commandLine.usage(System.out);
            System.exit(-1);
        }

        BootstrapContext context = new BootstrapContext(commandOptions, commandLine);
        PipelineGraphBuilder setupGraphBuilder = new PipelineGraphBuilder()
                .task(new EntryPointPipelineBuilderTask(context))
                .task(new LauncherPipelineBuilderTask(), "Entry")
                .task(new ReportPipelineBuilderTask(), "Launcher")
                .task(new LogPipelineBuilderTask(), "Report")
                .task(new ExportPipelineBuilderTask(), "Log")
                .task(new MakeStructuresPipelineBuilderTask(), "Export")
                .task(new AddChecksPipelineBuilderTask(), "Make");
        // Extend setup builder
        for (Pdg4jExtension extension : extensions) {
            extension.extendSetupPipeline(setupGraphBuilder, context);
        }

        new SimpleFlowPipeline("Setup")
                .run(setupGraphBuilder.build(), DIE_ON_FAILURE_LISTENER);

        PipelineGraphBuilder analysisGraphBuilder = context.getAnalysisGraphBuilder();
        // Extend analysis builder
        for (Pdg4jExtension extension : extensions) {
            extension.extendAnalysisPipeline(analysisGraphBuilder, context);
        }

        LoggerFactory.getLogger("Plugins")
                .info("Using {} plugin(-s) {}",
                        extensions.size(),
                        extensions.isEmpty() ?
                                "" :
                                ": " + extensions.stream()
                                        .map(Pdg4jExtension::getName)
                                        .collect(Collectors.joining(", ")));

        new SimpleFlowPipeline("PDG4J")
                .run(analysisGraphBuilder.build(), context.getAnalysisExecutionListener());
    }
}