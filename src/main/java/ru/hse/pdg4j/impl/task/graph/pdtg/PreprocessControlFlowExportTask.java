package ru.hse.pdg4j.impl.task.graph.pdtg;

import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.impl.task.util.IdleTask;
import spoon.reflect.declaration.CtMethod;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.failure;
import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class PreprocessControlFlowExportTask implements PipelineTask<IdleTask.Context> {
    private static final String pdtg = "PCFG";
    private final File destinationFolder;

    public PreprocessControlFlowExportTask(File destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

    @Override
    public String getName() {
        return "Export PCFG";
    }

    @Override
    public IdleTask.Context getContext() {
        return new IdleTask.Context();
    }

    @Override
    public PipelineTaskResult run(PipelineContext context) {
        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }

        var graphContext = context.getContext(PreprocessControlFlowTask.Context.class);
        for (Map.Entry<CtMethod<?>, ConditionalGraph> entry : graphContext.graphMap().entrySet()) {
            CtMethod<?> ctMethod = entry.getKey();

            ConditionalGraph controlFlowGraph = entry.getValue();
            File destination = new File(destinationFolder, pdtg + ctMethod.getSignature());
            if (!destination.exists()) {
                try {
                    destination.createNewFile();
                } catch (IOException e) {
                    return failure("Failed to create file for method " + ctMethod.getSignature() + ": " + e.getMessage());
                }
            }
            try (FileWriter writer = new FileWriter(destination)) {
                writer.write(controlFlowGraph.toGraphVisText());
            } catch (IOException e) {
                return failure("Failed to write to file: " + e.getMessage());
            }
        }

        return success();
    }

    @Override
    public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
        return List.of(PreprocessControlFlowTask.class);
    }
}
