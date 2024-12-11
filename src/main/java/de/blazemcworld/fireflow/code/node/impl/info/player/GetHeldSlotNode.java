package de.blazemcworld.fireflow.code.node.impl.info.player;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.item.Material;

public class GetHeldSlotNode extends Node {
    public GetHeldSlotNode() {
        super("get_held_slot", Material.SMOOTH_STONE);
        Input<PlayerValue> player = new Input<>("player", PlayerType.INSTANCE);
        Output<Double> slot = new Output<>("slot", NumberType.INSTANCE);

        slot.valueFrom(ctx -> {
            PlayerValue p = player.getValue(ctx);
            return p.available(ctx) ? p.get(ctx).getHeldSlot() + 1 : 0.0;
        });
    }

    @Override
    public Node copy() {
        return new GetHeldSlotNode();
    }
}
