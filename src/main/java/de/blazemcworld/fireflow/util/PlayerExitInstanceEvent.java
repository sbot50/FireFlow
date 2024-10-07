package de.blazemcworld.fireflow.util;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerExitInstanceEvent implements PlayerInstanceEvent {

    private final Player player;

    public PlayerExitInstanceEvent(Player player) {
        this.player = player;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }
}
