package de.blazemcworld.fireflow.space;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class SpaceManager {

    private static final HashMap<Integer, Space> spaces = new HashMap<>();

    static {
        MinecraftServer.getSchedulerManager().buildShutdownTask(() -> {
            for (Space space : spaces.values()) {
                space.unregister();
            }
        });
    }

    public static Space getSpace(int id) {
        return spaces.computeIfAbsent(id, Space::new);
    }

    public static void forget(int id) {
        spaces.remove(id);
    }

    public static @Nullable Space getSpace(Player player) {
        for (Space s : spaces.values()) {
            if (s.play.getPlayers().contains(player) || s.code.getPlayers().contains(player)) {
                return s;
            }
        }
        return null;
    }
}
