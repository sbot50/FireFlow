package de.blazemcworld.fireflow.code.node.impl.info.player;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.ConditionType;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.item.Material;

public class IsPlayerSneakingNode extends Node {
    public IsPlayerSneakingNode() {
        super("is_player_sneaking", Material.LEATHER_BOOTS);
        Input<PlayerValue> player = new Input<>("player", PlayerType.INSTANCE);
        Output<Boolean> sneaking = new Output<>("sneaking", ConditionType.INSTANCE);

        sneaking.valueFrom((ctx) -> {
            PlayerValue p = player.getValue(ctx);
            return p.available(ctx) && p.get(ctx).isSneaking();
        });
    }

    @Override
    public Node copy() {
        return new IsPlayerSneakingNode();
    }
}