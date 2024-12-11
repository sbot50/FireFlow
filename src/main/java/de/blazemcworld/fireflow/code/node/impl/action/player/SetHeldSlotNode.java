package de.blazemcworld.fireflow.code.node.impl.action.player;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.item.Material;

public class SetHeldSlotNode extends Node {
    public SetHeldSlotNode() {
        super("set_held_slot", Material.SMOOTH_STONE);
        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<PlayerValue> player = new Input<>("player", PlayerType.INSTANCE);
        Input<Double> slot = new Input<>("slot", NumberType.INSTANCE);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);
        signal.onSignal((ctx) -> {
            PlayerValue p = player.getValue(ctx);
            if (p.available(ctx)) p.get(ctx).setHeldItemSlot((byte) Math.clamp(slot.getValue(ctx).intValue() - 1, 0, 8));
            ctx.sendSignal(next);
        });
    }

    @Override
    public Node copy() {
        return new SetHeldSlotNode();
    }
}

