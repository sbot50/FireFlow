package de.blazemcworld.fireflow.code.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class ConditionType extends WireType<Boolean> {

    public static final ConditionType INSTANCE = new ConditionType();

    private ConditionType() {
    }

    @Override
    public String id() {
        return "condition";
    }

    @Override
    public Boolean defaultValue() {
        return false;
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.BLUE;
    }

    @Override
    public Boolean parseInset(String str) {
        if (str.equalsIgnoreCase("true")) return true;
        if (str.equalsIgnoreCase("false")) return false;
        return null;
    }

    @Override
    public Boolean convert(Object obj) {
        if (obj instanceof Boolean b) return b;
        return null;
    }

    @Override
    public JsonElement toJson(Boolean obj) {
        return new JsonPrimitive(obj);
    }

    @Override
    public Boolean fromJson(JsonElement json) {
        return json.getAsBoolean();
    }
}
