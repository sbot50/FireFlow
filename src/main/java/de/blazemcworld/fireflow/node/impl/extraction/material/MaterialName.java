package de.blazemcworld.fireflow.node.impl.extraction.material;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.util.TextCase;
import de.blazemcworld.fireflow.value.MaterialValue;
import de.blazemcworld.fireflow.value.TextValue;
import net.minestom.server.item.Material;

public class MaterialName extends ExtractionNode {

    public MaterialName() {
        super("Material Name", MaterialValue.INSTANCE, TextValue.INSTANCE);

        loadJava(MaterialName.class);
    }

    @FlowValueOutput("")
    private static String output() {
        return TextCase.namespaceToName(input().namespace());
    }

    @FlowValueInput("")
    private static Material input()  {
        throw new IllegalStateException();
    }

}
