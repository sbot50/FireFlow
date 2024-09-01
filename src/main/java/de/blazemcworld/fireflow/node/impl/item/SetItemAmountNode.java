package de.blazemcworld.fireflow.node.impl.item;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ItemValue;
import de.blazemcworld.fireflow.value.NumberValue;
import net.minestom.server.item.ItemStack;

public class SetItemAmountNode extends Node {
    public SetItemAmountNode() {
        super("Set Item Count");

        input("Item", ItemValue.INSTANCE);
        input("Count", NumberValue.INSTANCE);
        output("Result", ItemValue.INSTANCE);

        loadJava(SetItemAmountNode.class);
    }

    @FlowValueOutput("Result")
    private static ItemStack result() {
        return item().withAmount((int) count());
    }

    @FlowValueInput("Item")
    private static ItemStack item()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Count")
    private static double count()  {
        throw new IllegalStateException();
    }
}
