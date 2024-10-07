package de.blazemcworld.fireflow.code.type;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class NumberType extends WireType<Double> {

    public static final NumberType INSTANCE = new NumberType();

    private NumberType() {
    }

    @Override
    public Double defaultValue() {
        return 0.0;
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.GREEN;
    }
}
