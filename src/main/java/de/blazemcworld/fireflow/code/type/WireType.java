package de.blazemcworld.fireflow.code.type;

import net.kyori.adventure.text.format.TextColor;

public abstract class WireType<T> {

    public abstract String id();

    public abstract T defaultValue();

    public abstract TextColor getColor();

    public T parseInset(String str) {
        return null;
    }
}
