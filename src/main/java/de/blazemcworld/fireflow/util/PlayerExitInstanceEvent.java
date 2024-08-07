package de.blazemcworld.fireflow.util;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import org.jetbrains.annotations.NotNull;

public record PlayerExitInstanceEvent(Player player) implements PlayerInstanceEvent {
    @Override
    public @NotNull Player getPlayer() {
        return player;
    }
}
