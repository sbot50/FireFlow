package de.blazemcworld.fireflow.preferences;

import net.minestom.server.network.NetworkBuffer;

import java.util.HashMap;
import java.util.Map;

public class PlayerInfo {
    public Map<Preference, Integer> preferences = new HashMap<>();

    public void write(NetworkBuffer buffer) {
        buffer.write(NetworkBuffer.BYTE, (byte) preferences.size());
        for (Map.Entry<Preference, Integer> entry : preferences.entrySet()) {
            buffer.write(NetworkBuffer.STRING, entry.getKey().name());
            buffer.write(NetworkBuffer.BYTE, (byte) (int) entry.getValue());
        }
    }

    public void read(NetworkBuffer buffer) {
        int size = buffer.read(NetworkBuffer.BYTE);
        for (int i = 0; i < size; i++) {
            preferences.put(Preference.valueOf(buffer.read(NetworkBuffer.STRING)), (int) buffer.read(NetworkBuffer.BYTE));
        }
    }
}
