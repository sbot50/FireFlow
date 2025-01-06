package de.blazemcworld.fireflow.util;

import com.google.gson.Gson;
import de.blazemcworld.fireflow.FireFlow;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {

    public static final Store store = readConfig();

    private static Store readConfig() {
        Path file = Path.of("config.json").toAbsolutePath();
        if (!Files.exists(file)) {
            try (InputStream cfg = FireFlow.class.getClassLoader().getResourceAsStream("defaultConfig.json")) {
                if (!Files.exists(file.getParent())) Files.createDirectories(file.getParent());
                Files.copy(cfg, file);
            } catch (IOException e) {
                FireFlow.LOGGER.error("Failed to create default config!");
                throw new RuntimeException(e);
            }
        }

        try {
            return new Gson().fromJson(Files.readString(file), Store.class);
        } catch (IOException e) {
            FireFlow.LOGGER.error("Failed to read config!");
            throw new RuntimeException(e);
        }
    }

    public record Store(String motd, int port, LimitsConfig limits, String translations) {
    }

    public record LimitsConfig(long cpuUsage, int cpuHistory, int spacesPerPlayer, int totalSpaces) {
    }

}
