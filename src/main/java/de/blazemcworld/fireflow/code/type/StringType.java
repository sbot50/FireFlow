package de.blazemcworld.fireflow.code.type;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class StringType extends WireType<String> {

    public static final StringType INSTANCE = new StringType();

    private StringType() {
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
}
