package de.blazemcworld.fireflow.code.node.impl.info.player;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.item.Material;

public class GetPlayerFoodNode extends Node {
    public GetPlayerFoodNode() {
        super("get_player_food", Material.COOKED_BEEF);
        Input<PlayerValue> player = new Input<>("player", PlayerType.INSTANCE);
        Output<Double> food = new Output<>("food", NumberType.INSTANCE);

        food.valueFrom(ctx -> {
            PlayerValue p = player.getValue(ctx);
            return p.available(ctx) ? p.get(ctx).getFood() : 0.0;
        });
    }

    @Override
    public Node copy() {
        return new GetPlayerFoodNode();
    }
}
