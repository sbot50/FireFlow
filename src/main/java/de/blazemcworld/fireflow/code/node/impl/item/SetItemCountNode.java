package de.blazemcworld.fireflow.code.node.impl.item;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.ItemType;
import de.blazemcworld.fireflow.code.type.NumberType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class SetItemCountNode extends Node {

    public SetItemCountNode() {
        super("set_item_count", Material.BUNDLE);

        Input<ItemStack> item = new Input<>("item", ItemType.INSTANCE);
        Input<Double> count = new Input<>("count", NumberType.INSTANCE);
        Output<ItemStack> updated = new Output<>("updated", ItemType.INSTANCE);

        updated.valueFrom((ctx) -> item.getValue(ctx).withAmount(count.getValue(ctx).intValue()));
    }

    @Override
    public Node copy() {
        return new SetItemCountNode();
    }

}
