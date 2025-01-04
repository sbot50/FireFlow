package de.blazemcworld.fireflow.code.node.impl.string;

import de.blazemcworld.fireflow.code.node.Node;
import net.minestom.server.item.Material;
import de.blazemcworld.fireflow.code.type.StringType;
import de.blazemcworld.fireflow.code.type.NumberType;

public class StringLengthNode extends Node {
    public StringLengthNode() {
        super("string_length", Material.STICK);

        Input<String> string = new Input<>("string", StringType.INSTANCE);
        Output<Double> length = new Output<>("length", NumberType.INSTANCE);

        length.valueFrom(ctx -> (double) string.getValue(ctx).length());
    }

    @Override
    public Node copy() {
        return new StringLengthNode();
    }
}