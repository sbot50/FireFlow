package de.blazemcworld.fireflow.node.impl.extraction.material;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.MaterialValue;
import de.blazemcworld.fireflow.value.TextValue;
import net.minestom.server.item.Material;

import static de.blazemcworld.fireflow.util.CamelCase.camelCase;

public class MaterialName extends ExtractionNode {

    public MaterialName() {
        super("Material Name", MaterialValue.INSTANCE, TextValue.INSTANCE);

        loadJava(MaterialName.class);
    }

    @FlowValueOutput("")
    private static String output() {
        return camelCase(input().name().split(":")[1].replaceAll("_", " "));
    }

    @FlowValueInput("")
    private static Material input()  {
        throw new IllegalStateException();
    }

}
