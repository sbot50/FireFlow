package de.blazemcworld.fireflow.node.impl.item;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ItemValue;
import de.blazemcworld.fireflow.value.MessageValue;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;

import java.util.List;

public class AddItemLoreNode extends Node {
    public AddItemLoreNode() {
        super("Add Item Lore");

        input("Item", ItemValue.INSTANCE);
        input("Lore", MessageValue.INSTANCE);
        output("Result", ItemValue.INSTANCE);

        loadJava(AddItemLoreNode.class);
    }

    @FlowValueOutput("Result")
    private static ItemStack result() {
        ItemStack item = item();
        Component newLore = lore();
        List<Component> lore = item.get(ItemComponent.LORE);
        lore.add(newLore);
        return item.withLore(lore);
    }

    @FlowValueInput("Item")
    private static ItemStack item()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Lore")
    private static Component lore()  {
        throw new IllegalStateException();
    }
}
