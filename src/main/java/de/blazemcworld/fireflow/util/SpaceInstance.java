package de.blazemcworld.fireflow.util;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.CoordConversion;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpaceInstance extends InstanceContainer {

    public SpaceInstance() {
        super(UUID.randomUUID(), DimensionType.OVERWORLD);
        MinecraftServer.getInstanceManager().registerInstance(this);
    }

    @Override
    public void setBlock(int x, int y, int z, @NotNull Block block, boolean doBlockUpdates) {
        if (chunkNotInBounds(CoordConversion.globalToChunk(x), CoordConversion.globalToChunk(z))) return;
        super.setBlock(x, y, z, block, doBlockUpdates);
    }

    public static boolean chunkNotInBounds(int x, int z) {
        return Math.max(Math.abs(x), Math.abs(z)) >= Config.store.limits().spaceChunkDistance();
    }

    @Override
    public @NotNull CompletableFuture<Chunk> loadChunk(int chunkX, int chunkZ) {
        if (chunkNotInBounds(chunkX, chunkZ)) return CompletableFuture.completedFuture(null);
        return super.loadChunk(chunkX, chunkZ);
    }

    @Override
    public @NotNull CompletableFuture<Chunk> loadOptionalChunk(int chunkX, int chunkZ) {
        if (chunkNotInBounds(chunkX, chunkZ)) return CompletableFuture.completedFuture(null);
        return super.loadOptionalChunk(chunkX, chunkZ);
    }
}
