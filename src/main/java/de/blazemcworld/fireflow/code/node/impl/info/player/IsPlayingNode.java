package de.blazemcworld.fireflow.code.node.impl.info.player;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.ConditionType;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.item.Material;

public class IsPlayingNode extends Node {

    public IsPlayingNode() {
        super("is_playing", Material.OAK_SAPLING);

        Input<PlayerValue> player = new Input<>("player", PlayerType.INSTANCE);
        Output<Boolean> playing = new Output<>("playing", ConditionType.INSTANCE);

        playing.valueFrom((ctx) -> player.getValue(ctx).available(ctx));
    }

    @Override
    public Node copy() {
        return new IsPlayingNode();
    }
}
