package de.blazemcworld.fireflow.code.node.impl.action.player;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.VectorType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.Material;

public class SetPlayerVelocityNode extends Node {
    public SetPlayerVelocityNode() {
        super("set_player_velocity", Material.ARROW);
        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<PlayerValue> player = new Input<>("player", PlayerType.INSTANCE);
        Input<Vec> velocity = new Input<>("velocity", VectorType.INSTANCE);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);
        signal.onSignal((ctx) -> {
            PlayerValue p = player.getValue(ctx);
            if (p.available(ctx)) p.get(ctx).setVelocity(velocity.getValue(ctx));
            ctx.sendSignal(next);
        });
    }

    @Override
    public Node copy() {
        return new SetPlayerVelocityNode();
    }
}
