package de.blazemcworld.fireflow.code.type;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;

public class PlayerType extends WireType<Player> {

    public static final PlayerType INSTANCE = new PlayerType();

    private PlayerType() {
    }

    @Override
    public String id() {
        return "player";
    }

    @Override
    public Player defaultValue() {
        return null;
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.GOLD;
    }
}
