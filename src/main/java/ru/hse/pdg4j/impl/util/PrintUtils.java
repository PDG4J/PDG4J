package ru.hse.pdg4j.impl.util;

import ru.hse.pdg4j.api.PipelineGraphNode;

import java.util.ArrayDeque;
import java.util.Queue;

public class PrintUtils {
    // TODO: handle the case with single node (or multiple nodes without any connections to each other)
    public static String pipelineGraphToDot(PipelineGraphNode root) {
        StringBuilder builder = new StringBuilder();
        builder.append("graph {\n");

        Queue<PipelineGraphNode> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            PipelineGraphNode current = queue.remove();
            for (PipelineGraphNode child : current.getChildren()) {
                builder.append(current.getTask().getName())
                        .append(" -- ")
                        .append(child.getTask().getName())
                        .append("\n");
                queue.add(child);
            }
        }

        builder.append("}");
        return builder.toString();
    }
}
