package ru.hse.pdg4j;

import picocli.CommandLine;
import ru.hse.pdg4j.api.*;
import ru.hse.pdg4j.impl.SimpleFlowPipeline;
import ru.hse.pdg4j.impl.builder.PipelineGraphBuilder;
import ru.hse.pdg4j.impl.user.BootstrapContext;
import ru.hse.pdg4j.impl.user.CommandOptions;
import ru.hse.pdg4j.impl.user.check.AddChecksPipelineBuilderTask;
import ru.hse.pdg4j.impl.user.check.MakeStructuresPipelineBuilderTask;
import ru.hse.pdg4j.impl.user.entry.EntryPointPipelineBuilderTask;
import ru.hse.pdg4j.impl.user.export.ExportPipelineBuilderTask;
import ru.hse.pdg4j.impl.user.launcher.LauncherPipelineBuilderTask;
import ru.hse.pdg4j.impl.user.log.LogPipelineBuilderTask;
import ru.hse.pdg4j.impl.user.report.ReportPipelineBuilderTask;

public class Main {
    private static final ExecutionListener DIE_ON_FAILURE_LISTENER = new ExecutionListener() {
        @Override
        public void onException(PipelineTask<?> pipelineTask, PipelineGraphNode current, Exception e, PipelineContext context) {
            System.err.println("Failed to setup PDG4J: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    };

    public static void main(String[] args) {
        CommandOptions commandOptions = new CommandOptions();
        new CommandLine(commandOptions).parseArgs(args);

        BootstrapContext context = new BootstrapContext(commandOptions);
        new SimpleFlowPipeline("Setup").run(
                new PipelineGraphBuilder()
                        .task(new EntryPointPipelineBuilderTask(context))
                        .task(new LauncherPipelineBuilderTask(), "Entry")
                        .task(new ReportPipelineBuilderTask(), "Launcher")
                        .task(new LogPipelineBuilderTask(), "Report")
                        .task(new ExportPipelineBuilderTask(), "Log")
                        .task(new MakeStructuresPipelineBuilderTask(), "Export")
                        .task(new AddChecksPipelineBuilderTask(), "Make")
                        .build(),
                DIE_ON_FAILURE_LISTENER);

        PipelineGraphNode mainframe = context.getBuilder().build();
        new SimpleFlowPipeline("PDG4J").run(mainframe, context.getListener());
    }
}