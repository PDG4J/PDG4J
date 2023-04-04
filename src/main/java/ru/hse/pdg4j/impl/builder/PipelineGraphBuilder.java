package ru.hse.pdg4j.impl.builder;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ru.hse.pdg4j.api.PipelineGraphNode;
import ru.hse.pdg4j.api.PipelineTask;

import java.lang.reflect.Modifier;
import java.util.*;

public class PipelineGraphBuilder {
    private final Map<String, List<String>> graph;
    private final Map<String, PipelineTask<?>> nodeNames;
    private final Multimap<Class<? extends PipelineTask>, PipelineTask<?>> taskMap;

    public PipelineGraphBuilder() {
        this.graph = new HashMap<>();
        this.nodeNames = new HashMap<>();
        this.taskMap = ArrayListMultimap.create();
    }

    public PipelineGraphBuilder task(PipelineTask<?> task, String... additionalDependencies) {
        nodeNames.put(task.getName(), task);
        taskMap.put(task.getClass(), task);
        if (!graph.containsKey(task.getName())) {
            graph.put(task.getName(), new ArrayList<>());
        }
        for (String dependency : additionalDependencies) {
            if (!graph.containsKey(dependency)) {
                graph.put(dependency, new ArrayList<>(Collections.singleton(task.getName())));
            } else {
                graph.get(dependency).add(task.getName());
            }
        }
        return this;
    }

    public PipelineGraphNode build() {
        graph.forEach((s, strings) -> {
            if (!nodeNames.containsKey(s)) {
                throw new IllegalStateException("No task named '" + s + "' found");
            }
            for (String string : strings) {
                if (!nodeNames.containsKey(string)) {
                    throw new IllegalStateException("Could not find task named '" + string + "' (dependency for '" + s + "')");
                }
            }
        });

        for (PipelineTask<?> value : taskMap.values()) {
            for (Class<? extends PipelineTask<?>> requirement : value.getRequirements()) {
                List<Class<? extends PipelineTask>> implementations = new ArrayList<>();

                if (Modifier.isAbstract(requirement.getModifiers())) {
                    // Search through implementations
                    for (Class<? extends PipelineTask> keyClass : taskMap.keySet()) {
                        if (requirement.isAssignableFrom(keyClass)) {
                            implementations.add(keyClass);
                        }
                    }
                    if (implementations.isEmpty()) {
                        throw new IllegalStateException(
                                ("Could not find requirement for task named '%s'." +
                                        " Could not find any implementations for abstract %s")
                                        .formatted(value.getName(), requirement.getName()));
                    }
                } else {
                    implementations.add(requirement);
                }

                for (Class<? extends PipelineTask> implementation : implementations) {
                    if (!taskMap.containsKey(implementation)) {
                        throw new IllegalStateException(
                                "Could not find requirement for task named '%s'. Not found: %s"
                                        .formatted(value.getName(), implementation.getName()));
                    }
                    for (PipelineTask<?> implementationTask : taskMap.get(implementation)) {
                        if (!graph.containsKey(implementationTask.getName())) {
                            graph.put(implementationTask.getName(), new ArrayList<>(List.of(value.getName())));
                        } else {
                            graph.get(implementationTask.getName()).add(value.getName());
                        }
                    }
                }
            }
        }

        Set<String> visited = new HashSet<>();
        Stack<String> order = new Stack<>();

        for (String node : graph.keySet()) {
            if (!visited.contains(node)) {
                topSort(node, order, visited);
                visited.add(node);
            }
        }

        SimplePipelineGraphNode next = new SimplePipelineGraphNode(null);
        SimplePipelineGraphNode root = next;
        while (!order.empty()) {
            next.task = nodeNames.get(order.pop());
            if (order.size() != 0) {
                SimplePipelineGraphNode tail = new SimplePipelineGraphNode(null);
                next.addChild(tail);
                next = tail;
            }
        }

        return root;
    }

    private void topSort(String name, Stack<String> order, Set<String> visited) {
        for (String adjacent : graph.get(name)) {
            if (visited.contains(adjacent)) {
                continue;
            }
            topSort(adjacent, order, visited);
            visited.add(adjacent);
        }
        order.add(name);
    }

    private class SimplePipelineGraphNode implements PipelineGraphNode {
        private PipelineTask<?> task;
        private List<PipelineGraphNode> children;

        public SimplePipelineGraphNode(PipelineTask<?> task) {
            this.task = task;
            this.children = new ArrayList<>();
        }

        @Override
        public PipelineTask<?> getTask() {
            return task;
        }

        @Override
        public Collection<PipelineGraphNode> getChildren() {
            return children;
        }

        public void setChildren(List<PipelineGraphNode> children) {
            this.children = children;
        }

        public void addChild(PipelineGraphNode child) {
            children.add(child);
        }
    }
}
