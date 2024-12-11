package de.blazemcworld.fireflow.code.node.impl.info.player;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.item.Material;

public class GetExperiencePercentageNode extends Node {
    public GetExperiencePercentageNode() {
        super("get_experience_percentage", Material.EXPERIENCE_BOTTLE);
        Input<PlayerValue> player = new Input<>("player", PlayerType.INSTANCE);
        Output<Double> percentage = new Output<>("percentage", NumberType.INSTANCE);

        percentage.valueFrom(ctx -> {
            PlayerValue p = player.getValue(ctx);
            return p.available(ctx) ? p.get(ctx).getExp() * 100f : 0.0;
        });
    }

    @Override
    public Node copy() {
        return new GetExperiencePercentageNode();
    }
}

