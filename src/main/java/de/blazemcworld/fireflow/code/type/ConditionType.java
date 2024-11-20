package de.blazemcworld.fireflow.code.type;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class ConditionType extends WireType<Boolean> {

    public static final ConditionType INSTANCE = new ConditionType();

    private ConditionType() {
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
}
