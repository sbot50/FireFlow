package de.blazemcworld.fireflow.code.node.impl.world;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.StringType;
import de.blazemcworld.fireflow.code.type.VectorType;
import de.blazemcworld.fireflow.util.ChunkLoadingBlockBatch;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;

import java.util.concurrent.atomic.AtomicInteger;

public class SetRegionNode extends Node {
    public SetRegionNode() {
        super("set_region", Material.POLISHED_ANDESITE);
        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<Vec> corner1 = new Input<>("corner1", VectorType.INSTANCE);
        Input<Vec> corner2 = new Input<>("corner2", VectorType.INSTANCE);
        Input<String> block = new Input<>("block", StringType.INSTANCE);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);
        signal.onSignal((ctx) -> {
            Block placedBlock = Block.fromNamespaceId(block.getValue(ctx));
            if (placedBlock != null) {
                Vec corner1a = corner1.getValue(ctx);
                Vec corner2a = corner2.getValue(ctx);

                AtomicInteger curX = new AtomicInteger(corner1a.blockX());
                AtomicInteger curY = new AtomicInteger(corner1a.blockY());
                AtomicInteger curZ = new AtomicInteger(corner1a.blockZ());
                ChunkLoadingBlockBatch batch = new ChunkLoadingBlockBatch();
                MinecraftServer.getSchedulerManager().scheduleTask(() ->{
                    for (int chunkX = corner1a.blockX(); chunkX < corner2a.blockX(); chunkX+=1){
                        for (int chunkZ = corner1a.blockZ(); chunkZ < corner2a.blockZ(); chunkZ+=1){
                            for (int y = corner1a.blockY(); y <= corner2a.blockY(); y++) {
                                batch.setBlock(chunkX, y, chunkZ, placedBlock);
                            }

                        }
                    }
                    batch.apply(ctx.evaluator.space.play, null);

                    return TaskSchedule.stop();
                }, TaskSchedule.nextTick());
                batch.apply(ctx.evaluator.space.play, null);


            }

            ctx.sendSignal(next);
        });
    }

    @Override
    public Node copy() {
        return new SetRegionNode();
    }
}