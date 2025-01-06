package de.blazemcworld.fireflow.code.node.impl.world;

import de.blazemcworld.fireflow.code.CodeThread;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.StringType;
import de.blazemcworld.fireflow.code.type.VectorType;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;

public class SetRegionNode extends Node {
    public SetRegionNode() {
        super("set_region", Material.POLISHED_ANDESITE);
        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<Vec> corner1 = new Input<>("corner1", VectorType.INSTANCE);
        Input<Vec> corner2 = new Input<>("corner2", VectorType.INSTANCE);
        Input<String> block = new Input<>("block", StringType.INSTANCE);
        Output<Void> now = new Output<>("now", SignalType.INSTANCE);
        Output<Void> then = new Output<>("then", SignalType.INSTANCE);

        signal.onSignal((ctx) -> {
            Block placedBlock = Block.fromNamespaceId(block.getValue(ctx));
            if (placedBlock != null) {
                Vec corner1Value = corner1.getValue(ctx);
                Vec corner2Value = corner2.getValue(ctx);

                Vec min = corner1Value.min(corner2Value).max(Integer.MIN_VALUE, -64, Integer.MIN_VALUE);
                Vec max = corner1Value.max(corner2Value).min(Integer.MAX_VALUE, 319, Integer.MAX_VALUE);
                int[] chunk = { min.chunkX(), min.chunkZ() };

                int yStart = min.blockY();
                int yEnd = max.blockY() + 1;
                CodeThread worker = ctx.subThread();

                Runnable[] step = { null };
                step[0] = () -> {
                    if (ctx.evaluator.isStopped()) return;

                    if (ctx.evaluator.space.play.getChunk(chunk[0], chunk[1]) == null) {
                        ctx.evaluator.space.play.loadChunk(chunk[0], chunk[1]).thenRun(() -> {
                            MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
                                worker.submit(step[0]);
                                worker.resume();
                            });
                        });
                        return;
                    }

                    int xStart = Math.max(chunk[0] * 16, min.blockX());
                    int xEnd = Math.min(chunk[0] * 16 + 16, max.blockX() + 1);
                    int zStart = Math.max(chunk[1] * 16, min.blockZ());
                    int zEnd = Math.min(chunk[1] * 16 + 16, max.blockZ() + 1);

                    AbsoluteBlockBatch batch = new AbsoluteBlockBatch();
                    for (int x = xStart; x < xEnd; x++) {
                        for (int z = zStart; z < zEnd; z++) {
                            for (int y = yStart; y < yEnd; y++) {
                                batch.setBlock(x, y, z, placedBlock);
                            }
                        }
                    }

                    batch.apply(ctx.evaluator.space.play, () -> {
                        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
                            if (ctx.evaluator.remainingCpu() < 0) return TaskSchedule.nextTick();
                            chunk[0]++;
                            if (chunk[0] > max.chunkX()) {
                                chunk[0] = min.chunkX();
                                chunk[1]++;
                                if (chunk[1] > max.chunkZ()) {
                                    worker.sendSignal(then);
                                    worker.resume();
                                    return TaskSchedule.stop();
                                }
                            }

                            worker.submit(step[0]);
                            worker.resume();
                            return TaskSchedule.stop();
                        }, TaskSchedule.nextTick());
                    });
                };

                worker.submit(step[0]);
                worker.clearQueue();
            }
            ctx.sendSignal(now);
        });
    }

    @Override
    public Node copy() {
        return new SetRegionNode();
    }
}