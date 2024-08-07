package de.blazemcworld.fireflow.editor;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

public interface Widget {
    void remove();
    default Widget select(Player player, Vec cursor) {
        return null;
    }
    default void leftClick(Vec cursor, Player player, CodeEditor editor) {}
    default void rightClick(Vec cursor, Player player, CodeEditor editor) {}
}
