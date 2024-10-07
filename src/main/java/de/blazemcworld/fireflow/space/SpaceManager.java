package de.blazemcworld.fireflow.space;

import net.minestom.server.entity.Player;

import java.util.HashMap;

public class SpaceManager {

    private static final HashMap<Integer, Space> spaces = new HashMap<>();

    public static Space getOrNullSpace(int id) {
        return spaces.get(id);
    }

    public static Space getOrLoadSpace(int id) {
        Space space = spaces.get(id);
        if (space == null) {
            space = new Space(id);
            spaces.put(id, space);
        }
        return space;
    }

    public static Space getSpaceForPlayer(Player player) {
        for (Space space : spaces.values()) {
            if (space.play == player.getInstance()) return space;
            if (space.code == player.getInstance()) return space;
        }
        return null;
    }

}
