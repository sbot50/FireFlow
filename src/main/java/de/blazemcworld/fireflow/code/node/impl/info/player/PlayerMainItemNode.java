package de.blazemcworld.fireflow.code.node.impl.info.player;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.ItemType;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class PlayerMainItemNode extends Node {
    public PlayerMainItemNode() {
        super("player_main_item", Material.IRON_SHOVEL);

        Input<PlayerValue> player = new Input<>("player", PlayerType.INSTANCE);
        Output<ItemStack> item = new Output<>("item", ItemType.INSTANCE);

        item.valueFrom(ctx -> {
            PlayerValue p = player.getValue(ctx);
            return p.available(ctx) ? p.get(ctx).getItemInMainHand() : ItemStack.AIR;
        });
    }

    @Override
    public Node copy() {
        return new PlayerMainItemNode();
    }
}