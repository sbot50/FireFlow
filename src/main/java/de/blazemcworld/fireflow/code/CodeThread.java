package de.blazemcworld.fireflow.code;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionCallNode;

public class CodeThread {

    public final CodeEvaluator evaluator;
    private final HashMap<Node.Output<?>, Object> threadValues = new HashMap<>();
    private final List<Runnable> todo = new LinkedList<>();
    private long lastSync = System.nanoTime();
    public final VariableStore threadVariables = new VariableStore();
    public final Stack<FunctionCallNode> functionStack = new Stack<>();

    public CodeThread(CodeEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @SuppressWarnings("unchecked")
    public <T> T getThreadValue(Node.Output<T> out) {
        return (T) threadValues.get(out);
    }

    public <T> void setThreadValue(Node.Output<T> out, T value) {
        threadValues.put(out, value);
    }

    public void sendSignal(Node.Output<Void> signal) {
        todo.add(() -> signal.sendSignalImmediately(this));
    }

    public void clearQueue() {
        while (!todo.isEmpty()) {
            if (timelimitHit()) return;
            todo.removeFirst().run();
        }
    }

    public boolean timelimitHit() {
        long now = System.nanoTime();
        long elapsed = now - lastSync;
        lastSync = now;
        return evaluator.timelimitHit(elapsed);
    }

    public CodeThread subThread() {
        CodeThread thread = new CodeThread(evaluator);
        thread.threadValues.putAll(threadValues);
        return thread;
    }
}
