package de.blazemcworld.fireflow.preferences;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.timer.TaskSchedule;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class PlayerIndex {

    public static WeakHashMap<Player, PlayerInfo> playerMap = new WeakHashMap<>();
    public static final List<PlayerInfo> players = new ArrayList<>();
    private static final Path playerIndex = Path.of("player_index.bin");

    public static void init() {
        if (Files.exists(playerIndex)) read();

        MinecraftServer.getSchedulerManager().buildShutdownTask(PlayerIndex::write);

        MinecraftServer.getSchedulerManager().submitTask(() -> {
            write();
            return TaskSchedule.minutes(1);
        });
    }

    public static void add(Player player) {
        for (PlayerInfo info : players) {
            if (info.uuid.equals(player.getUuid())) {
                playerMap.put(player, info);
                return;
            }
        }
        PlayerInfo info = new PlayerInfo();
        info.uuid = player.getUuid();
        playerMap.put(player, info);
        players.add(info);
    }

    public static PlayerInfo get(Player player) {
        return playerMap.get(player);
    }

    private static void write() {
        NetworkBuffer buffer = new NetworkBuffer();

        buffer.write(NetworkBuffer.INT, players.size());
        for (PlayerInfo info : players) {
            info.write(buffer);
        }

        try {
            Files.write(playerIndex, buffer.readBytes(buffer.writeIndex()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void read() {
        NetworkBuffer buffer;
        try {
            buffer = new NetworkBuffer(ByteBuffer.wrap(Files.readAllBytes(playerIndex)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int count = buffer.read(NetworkBuffer.INT);
        for (int i = 0; i < count; i++) {
            PlayerInfo info = new PlayerInfo();
            info.read(buffer);
            players.add(info);
        }
    }

}
