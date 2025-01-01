package de.blazemcworld.fireflow.code.node.impl.item;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.ItemType;
import de.blazemcworld.fireflow.code.type.StringType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class CreateItemNode extends Node {

    public CreateItemNode() {
        super("create_item", Material.OAK_SAPLING);

        Input<String> type = new Input<>("type", StringType.INSTANCE);
        Output<ItemStack> item = new Output<>("item", ItemType.INSTANCE);

        item.valueFrom((ctx) -> {
            Material mat = Material.fromNamespaceId(type.getValue(ctx));
            return ItemStack.of(mat != null ? mat : Material.AIR);
        });
    }

    @Override
    public Node copy() {
        return new CreateItemNode();
    }
}
