package de.blazemcworld.fireflow.code;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionCallNode;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.TaskSchedule;

import java.util.*;

public class CodeThread {

    public final CodeEvaluator evaluator;
    private final HashMap<Node.Output<?>, Object> threadValues = new HashMap<>();
    private final Stack<Runnable> todo = new Stack<>();
    private long lastSync = System.nanoTime();
    public final VariableStore threadVariables = new VariableStore();
    public final Stack<FunctionCallNode> functionStack = new Stack<>();
    private boolean paused = false;

    public CodeThread(CodeEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @SuppressWarnings("unchecked")
    public <T> T getThreadValue(Node.Output<T> out) {
        Object v = threadValues.get(out);
        if (v == null) return out.type.defaultValue();
        return (T) threadValues.get(out);
    }

    public <T> void setThreadValue(Node.Output<T> out, T value) {
        threadValues.put(out, value);
    }

    public void sendSignal(Node.Output<Void> signal) {
        if (evaluator.space.debugger.isActive()) {
            evaluator.space.debugger.onSignal(signal, this);
        }
        todo.push(() -> signal.sendSignalImmediately(this));
    }

    public void submit(Runnable r) {
        todo.add(r);
    }

    public void clearQueue() {
        timelimitHit();
        while (!todo.isEmpty() && !paused) {
            todo.pop().run();
            timelimitHit();
        }
    }

    public void timelimitHit() {
        long now = System.nanoTime();
        long elapsed = now - lastSync;
        lastSync = now;
        if (evaluator.timelimitHit(elapsed) && !paused) {
            pause();
            MinecraftServer.getSchedulerManager().scheduleTask(() -> {
                if (evaluator.isStopped()) return TaskSchedule.stop();
                if (evaluator.remainingCpu() <= 0) return TaskSchedule.nextTick();
                resume();
                return TaskSchedule.stop();
            }, TaskSchedule.nextTick());
        }
    }

    public CodeThread subThread() {
        CodeThread thread = new CodeThread(evaluator);
        thread.threadValues.putAll(threadValues);
        return thread;
    }

    public void pause() {
        paused = true;
        timelimitHit();
    }

    public void resume() {
        paused = false;
        lastSync = System.nanoTime();
        clearQueue();
    }
}
