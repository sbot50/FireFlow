package de.blazemcworld.fireflow.code.node.impl.flow;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.NumberType;
import net.minestom.server.MinecraftServer;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;

import java.util.concurrent.atomic.AtomicInteger;

public class PauseThreadNode extends Node {
    public PauseThreadNode() {
        super("pause_thread", Material.RED_BED);

        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<Double> ticks = new Input<>("ticks", NumberType.INSTANCE);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);

        signal.onSignal((ctx) -> {
            ctx.pause();

            AtomicInteger remaining = new AtomicInteger(ticks.getValue(ctx).intValue());

            MinecraftServer.getSchedulerManager().submitTask(() -> {
                if (ctx.evaluator.isStopped()) return TaskSchedule.stop();
                if (remaining.getAndDecrement() <= 0) {
                    ctx.sendSignal(next);
                    ctx.resume();
                    return TaskSchedule.stop();
                }
                return TaskSchedule.tick(1);
            });
        });
    }

    @Override
    public Node copy() {
        return new PauseThreadNode();
    }
}