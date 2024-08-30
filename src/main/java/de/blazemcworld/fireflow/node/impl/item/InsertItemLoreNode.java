package de.blazemcworld.fireflow.node.impl.item;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ItemValue;
import de.blazemcworld.fireflow.value.MessageValue;
import de.blazemcworld.fireflow.value.NumberValue;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;

import java.util.List;

public class InsertItemLoreNode extends Node {
    public InsertItemLoreNode() {
        super("Insert Item Lore");

        input("Item", ItemValue.INSTANCE);
        input("Lore", MessageValue.INSTANCE);
        input("Position", NumberValue.INSTANCE);
        output("Result", ItemValue.INSTANCE);

        loadJava(InsertItemLoreNode.class);
    }

    @FlowValueOutput("Result")
    private static ItemStack result() {
        ItemStack item = item();
        List<Component> lore = item.get(ItemComponent.LORE);
        if (lore == null) lore = List.of();
        int position = (int) position();
        if (position < 0 || position > lore.size()) return item;
        Component lore1 = lore();
        lore.add(position, lore1);
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

    @FlowValueInput("Position")
    private static double position()  {
        throw new IllegalStateException();
    }
}
