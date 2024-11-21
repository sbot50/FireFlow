package de.blazemcworld.fireflow.code.value;

import java.util.UUID;

import de.blazemcworld.fireflow.code.CodeThread;
import de.blazemcworld.fireflow.space.Space;
import net.minestom.server.entity.Player;

public class PlayerValue {
    
    public final UUID uuid;

    public PlayerValue(Player player) {
        uuid = player.getUuid();
    }

    public PlayerValue(UUID uuid) {
        this.uuid = uuid;
    }

    public Player get(Space space) {
        return space.play.getPlayerByUuid(uuid);
    }

    public boolean available(Space space) {
        return get(space) != null;
    }

    public Player get(CodeThread ctx) {
        return get(ctx.evaluator.space);
    }

    public boolean available(CodeThread ctx) {
        return available(ctx.evaluator.space);
    }

}
