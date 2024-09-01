package de.blazemcworld.fireflow.node.impl.extraction.item;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ItemValue;
import de.blazemcworld.fireflow.value.NumberValue;
import net.minestom.server.item.ItemStack;

public class ItemCount extends ExtractionNode {

    public ItemCount() {
        super("Item Count", ItemValue.INSTANCE, NumberValue.INSTANCE);

        loadJava(ItemCount.class);
    }

    @FlowValueOutput("")
    private static double output() {
        return input().amount();
    }

    @FlowValueInput("")
    private static ItemStack input()  {
        throw new IllegalStateException();
    }

}
