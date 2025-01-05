package de.blazemcworld.fireflow.util;

import it.unimi.dsi.fastutil.Pair;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.CoordConversion;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ChunkLoadingBlockBatch extends AbsoluteBlockBatch {

    private final Set<Pair<Integer, Integer>> changedChunks = new HashSet<>();

    @Override
    public void setBlock(int x, int y, int z, @NotNull Block block) {
        changedChunks.add(Pair.of(CoordConversion.globalToChunk(x), CoordConversion.globalToChunk(z)));
        super.setBlock(x, y, z, block);
    }

    @Override
    protected AbsoluteBlockBatch apply(@NotNull Instance instance, @Nullable Runnable callback, boolean safeCallback) {
        Set<CompletableFuture<Chunk>> needed = new HashSet<>();
        for (Pair<Integer, Integer> chunk : changedChunks) {
            needed.add(instance.loadChunk(chunk.left(), chunk.right()));
        }

        CompletableFuture.allOf(needed.toArray(new CompletableFuture[0])).thenRun(() ->
                MinecraftServer.getSchedulerManager().scheduleEndOfTick(()
                        -> super.apply(instance, callback, safeCallback)));

        return null;
    }
}
