package de.blazemcworld.fireflow.code.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;

import java.util.UUID;

public class PlayerType extends WireType<PlayerValue> {

    public static final PlayerType INSTANCE = new PlayerType();

    private PlayerType() {
        super("player", NamedTextColor.GOLD, Material.PLAYER_HEAD);
    }

    @Override
    public PlayerValue defaultValue() {
        return new PlayerValue(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    @Override
    public PlayerValue convert(Object obj) {
        if (obj instanceof PlayerValue player) return player;
        return null;
    }

    @Override
    public JsonElement toJson(PlayerValue obj) {
        return new JsonPrimitive(obj.uuid.toString());
    }

    @Override
    public PlayerValue fromJson(JsonElement json) {
        return new PlayerValue(UUID.fromString(json.getAsString()));
    }

    @Override
    protected String stringifyInternal(PlayerValue value) {
        Player p = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(value.uuid);
        return p == null ? value.uuid.toString() : p.getUsername() + " (" + value.uuid + ")";
    }
}
