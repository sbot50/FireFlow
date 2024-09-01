package de.blazemcworld.fireflow.node.impl.extraction.material;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.MaterialValue;
import de.blazemcworld.fireflow.value.TextValue;
import net.minestom.server.item.Material;

public class MaterialID extends ExtractionNode {

    public MaterialID() {
        super("Material ID", MaterialValue.INSTANCE, TextValue.INSTANCE);

        loadJava(MaterialID.class);
    }

    @FlowValueOutput("")
    private static String output() {
        return input().name();
    }

    @FlowValueInput("")
    private static Material input()  {
        throw new IllegalStateException();
    }

}
