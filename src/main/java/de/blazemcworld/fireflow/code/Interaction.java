package de.blazemcworld.fireflow.code;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

public record Interaction(CodeEditor editor, Player player, Vec pos, Type type) {
    public enum Type {
        LEFT_CLICK, RIGHT_CLICK, SWAP_HANDS
    }
}
