package de.blazemcworld.fireflow.code.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class SignalType extends WireType<Void> {

    public static final SignalType INSTANCE = new SignalType();

    private SignalType() {
    }

    @Override
    public String id() {
        return "signal";
    }

    @Override
    public Void defaultValue() {
        return null;
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.AQUA;
    }

    @Override
    public Void convert(Object obj) {
        return null;
    }

    @Override
    public JsonElement toJson(Void obj) {
        return JsonNull.INSTANCE;
    }

    @Override
    public Void fromJson(JsonElement json) {
        return null;
    }
    
}
