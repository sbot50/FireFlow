package de.blazemcworld.fireflow;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FireFlow {

    public static final Logger LOGGER = LogManager.getLogger("FireFlow");

    public static void main(String[] args) {
        LOGGER.info("Starting...");
        MinecraftServer server = MinecraftServer.init();

        MojangAuth.init();
        ConsoleHandler.init();

        InstanceContainer inst = MinecraftServer.getInstanceManager().createInstanceContainer();

        inst.setChunkSupplier(LightingChunk::new);
        inst.setGenerator(unit -> {
            if (unit.absoluteStart().z() != 16.0) return;
            unit.modifier().fill(
                new BlockVec(0, 0, 0).add(unit.absoluteStart()),
                new BlockVec(16, 128, 1).add(unit.absoluteStart()),
                Block.POLISHED_BLACKSTONE
            );
        });

        EventNode<InstanceEvent> events = inst.eventNode();

        events.addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();
            player.setAllowFlying(true);
            player.setFlying(true);
        });

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(inst);
            event.getPlayer().setGameMode(GameMode.CREATIVE);
        });

        server.start("0.0.0.0", 25565);
        LOGGER.info("Ready!");
    }

}
