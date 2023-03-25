package ru.hse.pdg4j;

import ru.hse.pdg4j.api.*;
import ru.hse.pdg4j.impl.SimpleFlowPipeline;
import ru.hse.pdg4j.impl.builder.PipelineGraphBuilder;
import ru.hse.pdg4j.impl.task.basic.LauncherTask;
import ru.hse.pdg4j.impl.task.basic.MethodExtractionTask;
import ru.hse.pdg4j.impl.task.graph.cdg.ControlDependenceGraphTask;
import ru.hse.pdg4j.impl.task.graph.cfg.ControlDependenceGraphExportTask;
import ru.hse.pdg4j.impl.task.graph.cfg.ControlFlowGraphExportTask;
import ru.hse.pdg4j.impl.task.graph.cfg.ControlFlowGraphTask;

import java.io.File;
import ru.hse.pdg4j.impl.task.graph.pdtg.PostDominatorTreeExportTask;
import ru.hse.pdg4j.impl.task.graph.pdtg.PostDominatorTreeTask;
import ru.hse.pdg4j.impl.task.graph.pdtg.PreprocessControlFlowExportTask;
import ru.hse.pdg4j.impl.task.graph.pdtg.PreprocessControlFlowTask;

public class Main {
    public static void main(String[] args) {
        File sources = new File("src/test/sampleProject/src/main/java");
        File exportRoot = new File("src/test/sampleProject/out");
        String[] classpath = "src/test/sampleProject/build/dependencies/guava-24.0-jre.jar".split(";");

        FlowPipeline flowPipeline = new SimpleFlowPipeline("PDG4J");
        PipelineGraphNode graph = new PipelineGraphBuilder()
                .task(new LauncherTask(sources, classpath))
                .task(new MethodExtractionTask())
                .task(new ControlFlowGraphTask())
                .task(new ControlFlowGraphExportTask(exportRoot))
                .task(new PreprocessControlFlowTask("main"))
                .task(new PreprocessControlFlowExportTask(exportRoot))
                .task(new PostDominatorTreeTask("main"))
                .task(new PostDominatorTreeExportTask(exportRoot))
                .task(new ControlDependenceGraphTask("main"))
                .task(new ControlDependenceGraphExportTask(exportRoot))
                .build();

        flowPipeline.run(graph, new PipelineExecutionListener() {
            @Override
            public void onComplete(PipelineTask<?> pipelineTask, PipelineTaskResult result, PipelineGraphNode current) {
                System.out.printf("%s: %s%n",
                        pipelineTask.getName(),
                        result.isSuccessful()
                                ? "OK"
                                : "FAIL: " + result.getMessage());
            }

            @Override
            public void onException(PipelineTask<?> pipelineTask, PipelineGraphNode current, Exception e) {
                System.out.println("Error executing " + pipelineTask.getName());
                e.printStackTrace();
            }
        });
    }
}