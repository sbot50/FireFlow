package de.blazemcworld.fireflow.node.impl.item;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ItemValue;
import de.blazemcworld.fireflow.value.MessageValue;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;

public class SetItemNameNode extends Node {
    public SetItemNameNode() {
        super("Set Item Name");

        input("Item", ItemValue.INSTANCE);
        input("Name", MessageValue.INSTANCE);
        output("Result", ItemValue.INSTANCE);

        loadJava(SetItemNameNode.class);
    }

    @FlowValueOutput("Result")
    private static ItemStack result() {
        return item().withCustomName(name());
    }

    @FlowValueInput("Item")
    private static ItemStack item()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Name")
    private static Component name()  {
        throw new IllegalStateException();
    }
}
