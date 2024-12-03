package de.blazemcworld.fireflow.space;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.blazemcworld.fireflow.FireFlow;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;

public class SpaceManager {

    private static final HashMap<Integer, Space> spaces = new HashMap<>();
    public static final HashMap<Integer, SpaceInfo> info = new HashMap<>();
    public static int lastId = 0;

    public static void init() {
        load();

        MinecraftServer.getSchedulerManager().scheduleTask(SpaceManager::save, TaskSchedule.minutes(1), TaskSchedule.minutes(1));
        MinecraftServer.getSchedulerManager().buildShutdownTask(SpaceManager::save);
    }

    private static void save() {
        try {
            JsonObject data = new JsonObject();
            JsonObject spaces = new JsonObject();
            for (SpaceInfo spaceInfo : info.values()) {
                JsonObject space = new JsonObject();
                space.addProperty("name", spaceInfo.name);
                space.addProperty("icon", spaceInfo.icon.namespace().asString());
                space.addProperty("owner", spaceInfo.owner.toString());
                JsonArray contributors = new JsonArray();
                for (UUID contributor : spaceInfo.contributors) {
                    contributors.add(contributor.toString());
                }
                space.add("contributors", contributors);
                spaces.add(String.valueOf(spaceInfo.id), space);
            }
            data.add("spaces", spaces);
            data.addProperty("lastId", lastId);
            
            Files.writeString(Path.of("spaces.json"), data.toString());
        } catch (IOException e) {
            FireFlow.LOGGER.error("Failed to save spaces.json!", e);
        }

        for (Space space : List.copyOf(spaces.values())) {
            space.save();
            if (space.isInactive()) {
                spaces.remove(space.info.id);
                space.unload();
                FireFlow.LOGGER.info("Unloading space " + space.info.id);
            }
        }
    }

    private static void load() {
        try {
            if (!Files.exists(Path.of("spaces.json"))) return;

            JsonObject data = JsonParser.parseString(Files.readString(Path.of("spaces.json"))).getAsJsonObject();

            lastId = data.get("lastId").getAsInt();
            JsonObject spaces = data.getAsJsonObject("spaces");
            for (Map.Entry<String, JsonElement> raw : spaces.entrySet()) {
                JsonObject space = raw.getValue().getAsJsonObject();
                SpaceInfo spaceInfo = new SpaceInfo(Integer.parseInt(raw.getKey()));
                spaceInfo.name = space.get("name").getAsString();
                spaceInfo.icon = Material.fromNamespaceId(space.get("icon").getAsString());
                if (spaceInfo.icon == null) spaceInfo.icon = Material.PAPER;
                spaceInfo.owner = UUID.fromString(space.get("owner").getAsString());
                spaceInfo.contributors = new HashSet<>();
                for (JsonElement contributor : space.getAsJsonArray("contributors")) {
                    spaceInfo.contributors.add(UUID.fromString(contributor.getAsString()));
                }
                info.put(spaceInfo.id, spaceInfo);
            }
        } catch (IOException e) {
            FireFlow.LOGGER.error("Failed to load spaces.json!", e);
        }
    }

    public static Space getOrLoadSpace(SpaceInfo info) {
        Space space = spaces.get(info.id);
        if (space == null) {
            FireFlow.LOGGER.info("Loading space " + info.id);
            space = new Space(info);
            spaces.put(info.id, space);
        }
        return space;
    }

    public static Space getSpaceForPlayer(Player player) {
        for (Space space : spaces.values()) {
            if (space.play == player.getInstance()) return space;
            if (space.code == player.getInstance()) return space;
        }
        return null;
    }

}
