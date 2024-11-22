package de.blazemcworld.fireflow.code.node.impl.flow;

import java.util.concurrent.atomic.AtomicInteger;

import de.blazemcworld.fireflow.code.CodeThread;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.SignalType;
import net.minestom.server.MinecraftServer;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;

public class ScheduleNode extends Node {

    public ScheduleNode() {
        super("schedule", Material.CLOCK);

        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<Double> delay = new Input<>("delay", NumberType.INSTANCE);

        Output<Void> now = new Output<>("now", SignalType.INSTANCE);
        Output<Void> task = new Output<>("task", SignalType.INSTANCE);

        signal.onSignal((ctx) -> {
            AtomicInteger remaining = new AtomicInteger(delay.getValue(ctx).intValue());

            MinecraftServer.getSchedulerManager().submitTask(() -> {
                if (ctx.evaluator.isStopped()) return TaskSchedule.stop();
                CodeThread spawned = ctx.subThread();
                if (remaining.get() <= 0) {
                    spawned.sendSignal(task);
                    spawned.clearQueue();
                    return TaskSchedule.stop();
                }
                remaining.getAndDecrement();
                return TaskSchedule.tick(1);
            });

            ctx.sendSignal(now);
        });
    }

    @Override
    public Node copy() {
        return new ScheduleNode();
    }
}
