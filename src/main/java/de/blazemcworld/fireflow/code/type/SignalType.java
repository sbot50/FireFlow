package de.blazemcworld.fireflow.code.type;

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
}
