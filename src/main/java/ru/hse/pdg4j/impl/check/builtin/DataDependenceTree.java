package ru.hse.pdg4j.impl.check.builtin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jgrapht.alg.util.Pair;

public class DataDependenceTree {
    public Map<String, Pair<DataDependenceTree, DataDependenceTree>> dependencies;
    public Map<Integer, List<String>> mapping;
    public DataDependenceTree() {
        this.mapping = new HashMap<>();
        this.dependencies = new HashMap<>();
    }
}
