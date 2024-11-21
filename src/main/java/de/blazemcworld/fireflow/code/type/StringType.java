package de.blazemcworld.fireflow.code.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class StringType extends WireType<String> {

    public static final StringType INSTANCE = new StringType();

    private StringType() {
    }

    @Override
    public String id() {
        return "string";
    }

    @Override
    public String defaultValue() {
        return "";
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.YELLOW;
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
