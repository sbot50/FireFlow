package de.blazemcworld.fireflow.code.node.impl.number;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import net.minestom.server.item.Material;

public class ClampNumberNode extends Node {
    public ClampNumberNode() {
        super("clamp_number", Material.IRON_DOOR);
        Input<Double> number = new Input<>("input", NumberType.INSTANCE);
        Input<Double> min = new Input<>("min", NumberType.INSTANCE);
        Input<Double> max = new Input<>("max", NumberType.INSTANCE);
        Output<Double> output = new Output<>("output", NumberType.INSTANCE);

        output.valueFrom((ctx -> Math.clamp(number.getValue(ctx), min.getValue(ctx), max.getValue(ctx))));
    }

    @Override
    public Node copy() {
        return new ClampNumberNode();
    }
}