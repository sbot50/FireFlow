package de.blazemcworld.fireflow.code.node.impl.action.player;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.ConditionType;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.item.Material;

public class SetPlayerFlyingNode extends Node {
    public SetPlayerFlyingNode() {
        super("set_player_flying", Material.FEATHER);
        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<PlayerValue> player = new Input<>("player", PlayerType.INSTANCE);
        Input<Boolean> flying = new Input<>("flying", ConditionType.INSTANCE);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);
        signal.onSignal((ctx) -> {
            PlayerValue p = player.getValue(ctx);
            if (p.available(ctx)) p.get(ctx).setFlying(flying.getValue(ctx));
            ctx.sendSignal(next);
        });
    }

    @Override
    public Node copy() {
        return new SetPlayerFlyingNode();
    }
}
