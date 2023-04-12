package ru.hse.pdg4j.impl.task.graph.pdtg;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowEdge;
import fr.inria.controlflow.TransferFunctionVisitor;
import fr.inria.controlflow.Value;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ConditionalGraphNode {
    public static int count = 0;
    private final int id;
    ConditionalGraph parent;
    CtElement statement;

    private BranchKind kind;

    public ConditionalGraphNode(CtElement statement, ConditionalGraph parent, BranchKind kind) {
        this.kind = kind;
        this.parent = parent;
        this.statement = statement;
        ++count;
        this.id = count;
    }

    public ConditionalGraphNode(CtElement statement, ConditionalGraph parent) {
        this.statement = statement;
        this.parent = parent;
        ++count;
        this.id = count;
    }

    public BranchKind getKind() {
        return this.kind;
    }

    public CtElement getStatement() {
        return this.statement;
    }

    public String toString() {
        if (this.statement != null) {
            int var10000 = this.id;
            return var10000 + " - " + this.statement;
        } else {
            return this.kind + "_" + this.id;
        }
    }
}