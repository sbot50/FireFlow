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

public class SetBlockNode extends Node {
    public SetBlockNode() {
        super("set_block", Material.STONE);
        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<Vec> position = new Input<>("position", VectorType.INSTANCE);
        Input<String> block = new Input<>("block", StringType.INSTANCE);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);
        signal.onSignal((ctx) -> {
            Block placedBlock = Block.fromNamespaceId(block.getValue(ctx));
            if (placedBlock != null) {
                if (ctx.evaluator.space.spaceBlockBatch == null) {
                    ctx.evaluator.space.spaceBlockBatch = new ChunkLoadingBlockBatch();

                    MinecraftServer.getSchedulerManager().scheduleEndOfTick(() -> {
                        ctx.evaluator.space.spaceBlockBatch.apply(ctx.evaluator.space.play, null);
                        ctx.evaluator.space.spaceBlockBatch = null;
                    });
                }
                ctx.evaluator.space.spaceBlockBatch.setBlock(position.getValue(ctx), placedBlock);
            }
            ctx.sendSignal(next);
        });
    }

    @Override
    public Node copy() {
        return new SetBlockNode();
    }
}