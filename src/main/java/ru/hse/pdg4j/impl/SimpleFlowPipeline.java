package ru.hse.pdg4j.impl;

import ru.hse.pdg4j.api.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SimpleFlowPipeline implements FlowPipeline {
    private final String name;

    public SimpleFlowPipeline(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void run(PipelineGraphNode root, PipelineExecutionListener listener) {
        if (!isAcyclic(root)) {
            throw new IllegalArgumentException("Provided task graph contains a cycle");
        }

        Queue<PipelineGraphNode> nodes = new ArrayDeque<>();
        nodes.add(root);

        final AtomicReference<PipelineGraphNode> current = new AtomicReference<>(null);
        BasePipelineContext context = new BasePipelineContext() {
            @Override
            public Collection<PipelineTask<?>> getPending() {
                return nodes.stream().map(PipelineGraphNode::getTask).collect(Collectors.toList());
            }

            @Override
            public PipelineTask<?> getRunning() {
                return current.get().getTask();
            }
        };

        while (!nodes.isEmpty()) {
            current.set(nodes.remove());
            PipelineTask<?> task = current.get().getTask();
            try {
                for (Class<? extends PipelineTask<?>> requirement : task.getRequirements()) {
                    if (context.getTask(requirement) == null) {
                        throw new IllegalStateException(
                                "Requirement for task %s is not satisfied. Expected: %s"
                                        .formatted(task.getName(), requirement.getName()));
                    }
                }
                PipelineTaskResult result = task.run(context);
                context.put(task);
                listener.onComplete(task, result, current.get());
            } catch (Exception e) {
                listener.onException(task, current.get(), e);
            }
            nodes.addAll(current.get().getChildren());
        }
    }

    private boolean isAcyclic(PipelineGraphNode root) {
        Set<PipelineGraphNode> visited = new HashSet<>();
        Set<PipelineGraphNode> finished = new HashSet<>();

        Queue<PipelineGraphNode> nodes = new ArrayDeque<>();
        nodes.add(root);
        while (!nodes.isEmpty()) {
            PipelineGraphNode node = nodes.remove();
            if (isCreatingCycle(node, visited, finished)) {
                return false;
            }
            nodes.addAll(node.getChildren());
        }

        return true;
    }

    private boolean isCreatingCycle(PipelineGraphNode node, Set<PipelineGraphNode> visited, Set<PipelineGraphNode> finished) {
        visited.add(node);
        for (PipelineGraphNode child : node.getChildren()) {
            if (visited.contains(child)) {
                return true;
            }
            if (!finished.contains(child) && isCreatingCycle(child, visited, finished)) {
                return true;
            }
        }
        visited.remove(node);
        finished.add(node);
        return false;
    }
}
