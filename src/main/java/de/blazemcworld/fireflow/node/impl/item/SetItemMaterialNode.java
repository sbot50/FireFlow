package de.blazemcworld.fireflow.node.impl.item;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ItemValue;
import de.blazemcworld.fireflow.value.MaterialValue;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class SetItemMaterialNode extends Node {
    public SetItemMaterialNode() {
        super("Set Item Material");

        input("Item", ItemValue.INSTANCE);
        input("Material", MaterialValue.INSTANCE);
        output("Result", ItemValue.INSTANCE);

        loadJava(SetItemMaterialNode.class);
    }

    @FlowValueOutput("Result")
    private static ItemStack result() {
        return item().withMaterial(material());
    }

    @FlowValueInput("Item")
    private static ItemStack item()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Material")
    private static Material material()  {
        throw new IllegalStateException();
    }
}
