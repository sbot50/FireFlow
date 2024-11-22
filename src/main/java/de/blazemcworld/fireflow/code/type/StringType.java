package de.blazemcworld.fireflow.code.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.Material;

public class StringType extends WireType<String> {

    public static final StringType INSTANCE = new StringType();

    private StringType() {
        super("string", NamedTextColor.YELLOW, Material.STRING);
    }

    @Override
    public String defaultValue() {
        return "";
    }

    @Override
    public String parseInset(String str) {
        return str;
    }

    @Override
    public String convert(Object obj) {
        if (obj instanceof String str) return str;
        return null;
    }

    @Override
    public JsonElement toJson(String obj) {
        return new JsonPrimitive(obj);
    }

    @Override
    public String fromJson(JsonElement json) {
        return json.getAsString();
    }
}
