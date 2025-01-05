package de.blazemcworld.fireflow.code;

import de.blazemcworld.fireflow.FireFlow;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.node.Node.Varargs;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionCallNode;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionDefinition;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionInputsNode;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionOutputsNode;
import de.blazemcworld.fireflow.code.widget.NodeWidget;
import de.blazemcworld.fireflow.code.widget.Widget;
import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.util.Config;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.timer.TaskSchedule;

import java.util.*;

public class CodeEvaluator {

    public final Space space;
    public final EventNode<InstanceEvent> events;
    private boolean stopped = false;
    private long cpuUsedTick = 0;
    private long cpuUsedBefore = 0;
    public int cpuPercentage = 0;
    private final List<Long> cpuHistory = new LinkedList<>();
    public final VariableStore sessionVariables = new VariableStore();
    public final Set<Node> nodes;

    public CodeEvaluator(Space space) {
        this.space = space;

        Set<Node> nodes = new HashSet<>();
        for (Widget widget : space.editor.rootWidgets) {
            if (widget instanceof NodeWidget nodeWidget) {
                nodes.add(nodeWidget.node);
            }
        }

        events = EventNode.type("space-" + space.info.id, EventFilter.INSTANCE);
        space.play.eventNode().addChild(events);

        this.nodes = copyNodes(nodes);

        for (Node node : this.nodes) {
            node.init(this);
        }

        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            if (isStopped()) return TaskSchedule.stop();
            cpuPercentage = (int) ((cpuUsedBefore + cpuUsedTick) * 100 / Config.store.limits().cpuPerSecond());
            if (cpuUsedBefore + cpuUsedTick > Config.store.limits().cpuPerSecond()) {
                FireFlow.LOGGER.info("Space " + space.info.id + " used too much CPU: " + cpuPercentage + "%");
                space.reload("cpu");
                return TaskSchedule.stop();
            }
            cpuHistory.add(cpuUsedTick);
            if (cpuHistory.size() >= 20) cpuHistory.removeFirst();
            cpuUsedBefore = cpuHistory.stream().reduce(0L, Long::sum);
            cpuUsedTick = 0;
            return TaskSchedule.tick(1);
        }, TaskSchedule.tick(1));
    }

    public void stop() {
        space.play.eventNode().removeChild(events);
        stopped = true;
    }

    public boolean isStopped() {
        return stopped;
    }

    @SuppressWarnings("unchecked")
    private Set<Node> copyNodes(Set<Node> nodes) {
        HashMap<Node, Node> old2new = new HashMap<>();

        HashMap<String, FunctionDefinition> functions = new HashMap<>();

        for (FunctionDefinition old : space.editor.functions.values()) {
            FunctionDefinition copy = new FunctionDefinition(old.name, old.icon);
            for (Node.Output<?> input : old.inputsNode.outputs) {
                copy.addInput(input.id, input.type);
            }
            for (Node.Input<?> output : old.outputsNode.inputs) {
                copy.addOutput(output.id, output.type);
            }
            functions.put(old.name, copy);
        }

        for (Node node : nodes) {
            Node copy = null;
            
            if (node instanceof FunctionCallNode call) {
                copy = new FunctionCallNode(functions.get(call.function.name));
            }

            if (node instanceof FunctionInputsNode inputsNode) {
                copy = functions.get(inputsNode.function.name).inputsNode;
            }

            if (node instanceof FunctionOutputsNode outputsNode) {
                copy = functions.get(outputsNode.function.name).outputsNode;
            }

            if (copy == null) copy = node.copy();
            
            for (Varargs<?> base : node.varargs) {
                for (Varargs<?> next : copy.varargs) {
                    if (!base.id.equals(next.id)) continue;
                    next.ignoreUpdates = true;
                    copy.inputs.removeAll(next.children);
                    next.children.clear();

                    for (Node.Input<?> input : base.children) {
                        next.addInput(input.id);
                    }
                }
            }

            copy.clonedFrom = node;
            old2new.put(node, copy);
        }

        for (Node old : nodes) {
            Node copy = old2new.get(old);
            for (int i = 0; i < copy.inputs.size(); i++) {
                Node.Input<?> newInput = copy.inputs.get(i);
                Node.Output<?> oldTarget = old.inputs.get(i).connected;
                if (oldTarget == null) continue;
                Node.Output<?> newTarget = old2new.get(oldTarget.getNode()).outputs.get(oldTarget.getNode().outputs.indexOf(oldTarget));
                if (newTarget == null) continue;
                ((Node.Input<Object>) newInput).connect((Node.Output<Object>) newTarget);
            }

            for (int i = 0; i < copy.outputs.size(); i++) {
                Node.Output<?> newOutput = copy.outputs.get(i);
                Node.Input<?> oldTarget = old.outputs.get(i).connected;
                if (oldTarget == null) continue;
                Node.Input<?> newTarget = old2new.get(oldTarget.getNode()).inputs.get(oldTarget.getNode().inputs.indexOf(oldTarget));
                ((Node.Output<Object>) newOutput).connected = (Node.Input<Object>) newTarget;
            }

            for (int i = 0; i < copy.inputs.size(); i++) {
                Node.Input<?> newInput = copy.inputs.get(i);
                Node.Input<?> oldInput = old.inputs.get(i);
                if (oldInput.inset == null) continue;
                newInput.setInset(oldInput.inset);
            }
        }

        return new HashSet<>(old2new.values());
    }

    public CodeThread newCodeThread() {
        return new CodeThread(this);
    }

    public boolean timelimitHit(long elapsed) {
        cpuUsedTick += elapsed;
        return cpuUsedBefore + cpuUsedTick > Config.store.limits().cpuPerSecond();
    }
}
