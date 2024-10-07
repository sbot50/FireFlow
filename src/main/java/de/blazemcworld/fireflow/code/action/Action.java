package de.blazemcworld.fireflow.code.action;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

public interface Action {
    default void stop(CodeEditor editor, Player player) {}
    default void tick(Vec cursor, CodeEditor editor, Player player) {}
    default void interact(Interaction i) {}
}
