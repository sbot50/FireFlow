package de.blazemcworld.fireflow.node.impl.extraction.item;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ItemValue;
import de.blazemcworld.fireflow.value.MaterialValue;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class ItemMaterial extends ExtractionNode {

    public ItemMaterial() {
        super("Item Material", ItemValue.INSTANCE, MaterialValue.INSTANCE);

        loadJava(ItemMaterial.class);
    }

    @FlowValueOutput("")
    private static Material output() {
        return input().material();
    }

    @FlowValueInput("")
    private static ItemStack input()  {
        throw new IllegalStateException();
    }

}
