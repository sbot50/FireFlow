package de.blazemcworld.fireflow.code.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class NumberType extends WireType<Double> {

    public static final NumberType INSTANCE = new NumberType();

    private NumberType() {
    }

    @Override
    public String id() {
        return "number";
    }

    @Override
    public Double defaultValue() {
        return 0.0;
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.GREEN;
    }

    @Override
    public Double parseInset(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public Double convert(Object obj) {
        if (obj instanceof Double d) return d;
        return null;
    }

    @Override
    public JsonElement toJson(Double obj) {
        return new JsonPrimitive(obj);
    }

    @Override
    public Double fromJson(JsonElement json) {
        return json.getAsDouble();
    }
}
