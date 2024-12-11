package de.blazemcworld.fireflow.code.node.impl.action.player;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.item.Material;

public class SetExperiencePercentageNode extends Node {
    public SetExperiencePercentageNode() {
        super("set_experience_percentage", Material.EXPERIENCE_BOTTLE);
        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<PlayerValue> player = new Input<>("player", PlayerType.INSTANCE);
        Input<Double> percentage = new Node.Input<>("percentage", NumberType.INSTANCE);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);
        signal.onSignal((ctx) -> {
            PlayerValue p = player.getValue(ctx);
            if (p.available(ctx)) p.get(ctx).setExp(percentage.getValue(ctx).floatValue() / 100f);
            ctx.sendSignal(next);
        });
    }

    @Override
    public Node copy() {
        return new SetExperiencePercentageNode();
    }
}

