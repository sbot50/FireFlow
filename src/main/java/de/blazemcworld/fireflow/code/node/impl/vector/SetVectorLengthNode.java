package de.blazemcworld.fireflow.code.node.impl.vector;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.VectorType;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.Material;

public class SetVectorLengthNode extends Node {
    public SetVectorLengthNode() {
        super("set_vector_length", Material.SHEARS);
        Input<Vec> vector = new Input<>("vector", VectorType.INSTANCE);
        Input<Double> length = new Input<>("length", NumberType.INSTANCE);
        Output<Vec> scaled = new Output<>("scaled", VectorType.INSTANCE);

        scaled.valueFrom(ctx -> {
            Vec v = vector.getValue(ctx);
            double l = length.getValue(ctx);
            return v.normalize().mul(l);
        });
    }

    @Override
    public Node copy() {
        return new SetVectorLengthNode();
    }
}
