package de.blazemcworld.fireflow.code.node.impl.action.player;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.item.Material;

public class SetExperienceLevelNode extends Node {
    public SetExperienceLevelNode() {
        super("set_experience_level", Material.ENCHANTING_TABLE);
        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<PlayerValue> player = new Input<>("player", PlayerType.INSTANCE);
        Input<Double> level = new Input<>("level", NumberType.INSTANCE);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);
        signal.onSignal((ctx) -> {
            PlayerValue p = player.getValue(ctx);
            if (p.available(ctx)) p.get(ctx).setLevel(level.getValue(ctx).intValue());
            ctx.sendSignal(next);
        });
    }

    @Override
    public Node copy() {
        return new SetExperienceLevelNode();
    }
}