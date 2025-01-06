package de.blazemcworld.fireflow.code.node.impl.event.space;

import de.blazemcworld.fireflow.code.CodeEvaluator;
import de.blazemcworld.fireflow.code.CodeThread;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.SignalType;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.instance.InstanceChunkLoadEvent;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;

public class OnChunkLoadNode extends Node {

    private final Output<Void> signal;
    private final Output<Double> x;
    private final Output<Double> z;

    public OnChunkLoadNode() {
        super("on_chunk_load", Material.GRASS_BLOCK);

        signal = new Output<>("signal", SignalType.INSTANCE);
        x = new Output<>("x", NumberType.INSTANCE);
        z = new Output<>("z", NumberType.INSTANCE);
        x.valueFromThread();
        z.valueFromThread();
    }

    @Override
    public void init(CodeEvaluator evaluator) {
        evaluator.events.addListener(InstanceChunkLoadEvent.class, event -> {
            MinecraftServer.getSchedulerManager().scheduleTask(() -> {
                CodeThread thread = evaluator.newCodeThread();
                thread.setThreadValue(x, event.getChunkX() * 16.0);
                thread.setThreadValue(z, event.getChunkZ() * 16.0);
                thread.sendSignal(signal);
                thread.clearQueue();
            }, TaskSchedule.tick(3), TaskSchedule.stop());
        });
    }

    @Override
    public Node copy() {
        return new OnChunkLoadNode();
    }

}
