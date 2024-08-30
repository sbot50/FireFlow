package de.blazemcworld.fireflow.node.impl.extraction.item;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ItemValue;
import de.blazemcworld.fireflow.value.MessageValue;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;

public class ItemName extends ExtractionNode {

    public ItemName() {
        super("Item Name", ItemValue.INSTANCE, MessageValue.INSTANCE);

        loadJava(ItemName.class);
    }

    @FlowValueOutput("")
    private static Component output() {
        return input().get(ItemComponent.CUSTOM_NAME);
    }

    @FlowValueInput("")
    private static ItemStack input()  {
        throw new IllegalStateException();
    }

}
