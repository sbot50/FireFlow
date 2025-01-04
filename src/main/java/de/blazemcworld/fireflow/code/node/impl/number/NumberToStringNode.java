package de.blazemcworld.fireflow.code.node.impl.number;

import de.blazemcworld.fireflow.code.node.Node;
import net.minestom.server.item.Material;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.StringType;

public class NumberToStringNode extends Node {
    public NumberToStringNode() {
        super("number_to_string", Material.STRING);

        Input<Double> number = new Input<>("number", NumberType.INSTANCE);
        Output<String> string = new Output<>("string", StringType.INSTANCE);

        string.valueFrom((ctx -> number.getValue(ctx).toString()));
    }

    @Override
    public Node copy() {
        return new NumberToStringNode();
    }
}