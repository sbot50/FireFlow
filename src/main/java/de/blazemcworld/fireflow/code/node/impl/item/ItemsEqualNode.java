package de.blazemcworld.fireflow.code.node.impl.item;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.ConditionType;
import de.blazemcworld.fireflow.code.type.ItemType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class ItemsEqualNode extends Node {
    public ItemsEqualNode() {
        super("items_equal", Material.ANVIL);

        Input<ItemStack> first = new Input<>("First", ItemType.INSTANCE);
        Input<ItemStack> second = new Input<>("second", ItemType.INSTANCE);
        Input<Boolean> checkCount = new Input<>("check_count", ConditionType.INSTANCE);
        Output<Boolean> isCase = new Output<>("case", ConditionType.INSTANCE);

        isCase.valueFrom((ctx) -> {
            ItemStack firstItem = first.getValue(ctx);
            ItemStack secondItem = second.getValue(ctx);
            return firstItem.isSimilar(secondItem) && (!checkCount.getValue(ctx) || firstItem.amount() == secondItem.amount());
        });
    }

    @Override
    public Node copy() {
        return new ItemsEqualNode();
    }
}