package ru.hse.pdg4j;

import fr.inria.controlflow.ControlFlowBuilder;
import ru.hse.pdg4j.api.*;
import ru.hse.pdg4j.impl.SimpleFlowPipeline;
import ru.hse.pdg4j.impl.builder.PipelineGraphBuilder;
import ru.hse.pdg4j.impl.task.graph.cfg.ControlFlowGraphExportTask;
import ru.hse.pdg4j.impl.task.graph.cfg.ControlFlowGraphTask;
import ru.hse.pdg4j.impl.task.basic.LauncherTask;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File sources = new File("C:\\Users\\me\\IdeaProjects\\PDG4J\\src\\test\\sampleProject\\src\\main\\java");
        File exportRoot = new File("C:\\Users\\me\\IdeaProjects\\PDG4J\\src\\test\\sampleProject\\out");
        String[] classpath = "C:\\Users\\me\\IdeaProjects\\PDG4J\\src\\test\\sampleProject\\build\\dependencies\\guava-24.0-jre.jar".split(";");

        FlowPipeline flowPipeline = new SimpleFlowPipeline("PDG4J");
        PipelineGraphNode graph = new PipelineGraphBuilder()
                .task(new LauncherTask(sources, classpath))
                .task(new ControlFlowGraphTask())
                .task(new ControlFlowGraphExportTask(exportRoot))
                .build();

        flowPipeline.run(graph, new PipelineExecutionListener() {
            @Override
            public void onComplete(PipelineTask<?> pipelineTask, PipelineTaskResult result, PipelineGraphNode current) {
                System.out.println("COMPLETED " + pipelineTask.getName());
            }

            @Override
            public void onException(PipelineTask<?> pipelineTask, PipelineGraphNode current, Exception e) {
                System.out.println("Whoops " + pipelineTask.getName());
                e.printStackTrace();
            }
        });
    }

    public static void main1(String[] args) {
        Launcher launcher = new Launcher();
        launcher.addInputResource("C:\\Users\\me\\IdeaProjects\\PDG4J\\src\\test\\sampleProject\\src");
        launcher.getEnvironment().setComplianceLevel(11);
        launcher.getEnvironment().setShouldCompile(true);
        launcher.getEnvironment().setCommentEnabled(true);
        launcher.run();
        SpoonModelBuilder compiler = launcher.createCompiler();
        boolean success = compiler.compile(SpoonModelBuilder.InputType.CTTYPES);
        System.out.println(success);
        for (CtType<?> clazz : launcher.getFactory().Class().getAll()) {
            System.out.println("CFG for clazz " + clazz.getQualifiedName());
            for (CtMethod<?> method : clazz.getMethods()) {
                System.out.println("Of method " + method.getSimpleName() + ":");
                System.out.println(new ControlFlowBuilder().build(method).toGraphVisText());
                System.out.println();
            }
        }
    }
}