package de.blazemcworld.fireflow.node.impl.extraction.item;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ItemValue;
import de.blazemcworld.fireflow.value.ListValue;
import de.blazemcworld.fireflow.value.MessageValue;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;

import java.util.List;

public class ItemLore extends ExtractionNode {

    public ItemLore() {
        super("Item Lore", ItemValue.INSTANCE, ListValue.get(MessageValue.INSTANCE));

        loadJava(ItemLore.class);
    }

    @FlowValueOutput("")
    private static List<Component> output() {
        return input().get(ItemComponent.LORE);
    }

    @FlowValueInput("")
    private static ItemStack input()  {
        throw new IllegalStateException();
    }

}
