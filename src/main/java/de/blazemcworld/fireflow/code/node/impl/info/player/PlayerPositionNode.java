package de.blazemcworld.fireflow.code.node.impl.info.player;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.type.PositionType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.item.Material;

public class PlayerPositionNode extends Node {

    public PlayerPositionNode() {
        super("player_position", Material.RECOVERY_COMPASS);

        Input<PlayerValue> player = new Input<>("player", PlayerType.INSTANCE);
        Output<Pos> position = new Output<>("position", PositionType.INSTANCE);

        position.valueFrom((ctx) -> {
            PlayerValue p = player.getValue(ctx);
            if (p.available(ctx)) return p.get(ctx).getPosition();
            return Pos.ZERO;
        });
    }

    @Override
    public PlayerPositionNode copy() {
        return new PlayerPositionNode();
    }

}
