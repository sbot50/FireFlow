package de.blazemcworld.fireflow.preferences;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.timer.TaskSchedule;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerIndex {

    public static final Map<UUID, PlayerInfo> players = new HashMap<>();
    private static final Path playerIndex = Path.of("player_index.bin");

    public static void init() {
        if (Files.exists(playerIndex)) read();

        MinecraftServer.getSchedulerManager().buildShutdownTask(PlayerIndex::write);

        MinecraftServer.getSchedulerManager().submitTask(() -> {
            write();
            return TaskSchedule.minutes(1);
        });
    }

    public static PlayerInfo get(Player player) {
        return players.computeIfAbsent(player.getUuid(), id -> new PlayerInfo());
    }

    private static void write() {
        NetworkBuffer buffer = new NetworkBuffer();

        buffer.write(NetworkBuffer.INT, players.size());
        for (Map.Entry<UUID, PlayerInfo> entry : players.entrySet()) {
            buffer.write(NetworkBuffer.UUID, entry.getKey());
            entry.getValue().write(buffer);
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
            UUID uuid = buffer.read(NetworkBuffer.UUID);
            PlayerInfo info = new PlayerInfo();
            info.read(buffer);
            players.put(uuid, info);
        }
    }

}
