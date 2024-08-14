package de.blazemcworld.fireflow.preferences;

import net.minestom.server.network.NetworkBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInfo {
    public Map<Preference, Integer> preferences = new HashMap<>();
    public UUID uuid = null;

    public void write(NetworkBuffer buffer) {
        buffer.write(NetworkBuffer.UUID, uuid);
        buffer.write(NetworkBuffer.INT, preferences.size());
        for (Map.Entry<Preference, Integer> entry : preferences.entrySet()) {
            buffer.write(NetworkBuffer.STRING, entry.getKey().name());
            buffer.write(NetworkBuffer.INT, entry.getValue());
        }
    }

    public void read(NetworkBuffer buffer) {
        uuid = buffer.read(NetworkBuffer.UUID);
        int size = buffer.read(NetworkBuffer.INT);
        for (int i = 0; i < size; i++) {
            preferences.put(Preference.valueOf(buffer.read(NetworkBuffer.STRING)), buffer.read(NetworkBuffer.INT));
        }
    }
}
