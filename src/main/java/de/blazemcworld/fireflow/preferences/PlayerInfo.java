package de.blazemcworld.fireflow.preferences;

import net.minestom.server.network.NetworkBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInfo {
    private static final DeletePreference DELETE_PREFERENCE = new DeletePreference();

    public Map<String, Integer> preferences = new HashMap<>(
            Map.of(
                    "preference-delete", 0 //default value
            )
    );
    public UUID uuid = null;
    public static final Map<String, Preference> preferenceTranslator = Map.of(
            "preference-delete", DELETE_PREFERENCE
    );

    public void write(NetworkBuffer buffer) {
        buffer.write(NetworkBuffer.UUID, uuid);
        for (Map.Entry<String, Integer> entry : preferences.entrySet()) {
            buffer.write(NetworkBuffer.STRING, entry.getKey());
            buffer.write(NetworkBuffer.INT, entry.getValue());
        }
    }

    public void read(NetworkBuffer buffer) {
        uuid = buffer.read(NetworkBuffer.UUID);
        for (int i = 0; i < preferences.size(); i++) {
            preferences.put(buffer.read(NetworkBuffer.STRING), buffer.read(NetworkBuffer.INT));
        }
    }
}
