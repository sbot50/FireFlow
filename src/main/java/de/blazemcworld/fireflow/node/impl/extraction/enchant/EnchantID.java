package de.blazemcworld.fireflow.node.impl.extraction.enchant;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.EnchantmentValue;
import de.blazemcworld.fireflow.value.TextValue;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.registry.DynamicRegistry;

public class EnchantID extends ExtractionNode {

    public EnchantID() {
        super("Enchantment ID", EnchantmentValue.INSTANCE, TextValue.INSTANCE);

        loadJava(EnchantID.class);
    }

    @FlowValueOutput("")
    private static String output() {
        return input().name();
    }

    @FlowValueInput("")
    private static DynamicRegistry.Key<Enchantment> input()  {
        throw new IllegalStateException();
    }

}
