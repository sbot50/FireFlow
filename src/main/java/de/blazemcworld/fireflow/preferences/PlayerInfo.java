package de.blazemcworld.fireflow.preferences;

import net.minestom.server.network.NetworkBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInfo {
    public Map<Preference, Integer> preferences = new HashMap<>(
            Map.of(
                    Preference.DELETE, 0 //default value
            )
    );
    public UUID uuid = null;

    public void write(NetworkBuffer buffer) {
        buffer.write(NetworkBuffer.UUID, uuid);
        for (Map.Entry<Preference, Integer> entry : preferences.entrySet()) {
            buffer.write(NetworkBuffer.STRING, entry.getKey().name());
            buffer.write(NetworkBuffer.INT, entry.getValue());
        }
    }

    public void read(NetworkBuffer buffer) {
        uuid = buffer.read(NetworkBuffer.UUID);
        for (int i = 0; i < preferences.size(); i++) {
            preferences.put(Preference.valueOf(buffer.read(NetworkBuffer.STRING)), buffer.read(NetworkBuffer.INT));
        }
    }
}
