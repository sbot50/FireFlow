package de.blazemcworld.fireflow.util;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;

public class Transfer {

    public static void move(Player player, InstanceContainer destination) {
        MinecraftServer.getGlobalEventHandler().call(new PlayerExitInstanceEvent(player));
        player.respawn();
        Statistics.reset(player);
        player.setInstance(destination, Pos.ZERO);
    }

}
