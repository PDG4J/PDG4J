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
    List<Value> input;
    List<Value> output;
    Object tag;
    TransferFunctionVisitor visitor;
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

    public void setKind(BranchKind kind) {
        this.kind = kind;
    }

    public void transfer(TransferFunctionVisitor visitor) {
        this.visitor = visitor;
        this.transfer();
    }

    public void transfer() {
        if (this.statement != null && this.visitor != null) {
            this.output = this.visitor.transfer(this.statement);
        } else {
            throw new RuntimeException("Unable to perform the transfer function. Statement or visitor are null.");
        }
    }

    public int getId() {
        return this.id;
    }

    public List<fr.inria.controlflow.ControlFlowNode> siblings() {
        ArrayList<fr.inria.controlflow.ControlFlowNode> result = new ArrayList();
        Iterator var2 = this.prev().iterator();

        while (var2.hasNext()) {
            fr.inria.controlflow.ControlFlowNode n = (fr.inria.controlflow.ControlFlowNode) var2.next();
            Iterator var4 = n.next().iterator();

            while (var4.hasNext()) {
                fr.inria.controlflow.ControlFlowNode nn = (fr.inria.controlflow.ControlFlowNode) var4.next();
                if (!nn.equals(this)) {
                    result.add(nn);
                }
            }
        }

        return result;
    }

    public List<fr.inria.controlflow.ControlFlowNode> next() {
        ArrayList<fr.inria.controlflow.ControlFlowNode> result = new ArrayList();
        Iterator var2 = this.parent.outgoingEdgesOf(this).iterator();

        while (var2.hasNext()) {
            ControlFlowEdge e = (ControlFlowEdge) var2.next();
            result.add(e.getTargetNode());
        }

        return result;
    }

    public List<fr.inria.controlflow.ControlFlowNode> prev() {
        ArrayList<fr.inria.controlflow.ControlFlowNode> result = new ArrayList();
        Iterator var2 = this.parent.incomingEdgesOf(this).iterator();

        while (var2.hasNext()) {
            ControlFlowEdge e = (ControlFlowEdge) var2.next();
            result.add(e.getSourceNode());
        }

        return result;
    }

    public List<Value> getOutput() {
        if (this.output == null) {
            this.transfer();
        }

        return this.output;
    }

    public CtElement getStatement() {
        return this.statement;
    }

    public void setStatement(CtElement statement) {
        this.statement = statement;
    }

    public List<Value> getInput() {
        return this.input;
    }

    public void setInput(List<Value> input) {
        this.input = input;
    }

    public ConditionalGraph getParent() {
        return this.parent;
    }

    public void setParent(ConditionalGraph parent) {
        this.parent = parent;
    }

    public Object getTag() {
        return this.tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
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