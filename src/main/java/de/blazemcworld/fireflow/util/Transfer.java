package de.blazemcworld.fireflow.util;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;

public class Transfer {

    public static void movePlayer(Player player, InstanceContainer instance) {
        MinecraftServer.getGlobalEventHandler().call(new PlayerExitInstanceEvent(player));
        Statistics.reset(player);
        player.setInstance(instance, Pos.ZERO);
    }

}
