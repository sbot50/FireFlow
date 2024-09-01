package de.blazemcworld.fireflow.node.impl.item;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ItemValue;
import de.blazemcworld.fireflow.value.NumberValue;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;

import java.util.List;

public class RemoveItemLoreNode extends Node {
    public RemoveItemLoreNode() {
        super("Remove Item Lore");

        input("Item", ItemValue.INSTANCE);
        input("Position", NumberValue.INSTANCE);
        output("Result", ItemValue.INSTANCE);

        loadJava(RemoveItemLoreNode.class);
    }

    @FlowValueOutput("Result")
    private static ItemStack result() {
        ItemStack item = item();
        List<Component> lore = item.get(ItemComponent.LORE);
        if (lore == null) lore = List.of();
        int position = (int) position();
        if (position > 0 || position < lore.size()) lore.remove(position);
        return item.withLore(lore);
    }

    @FlowValueInput("Item")
    private static ItemStack item()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Position")
    private static double position()  {
        throw new IllegalStateException();
    }
}
