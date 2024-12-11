package de.blazemcworld.fireflow.code.node.impl.vector;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.VectorType;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.Material;

public class UnpackVectorNode extends Node {

    public UnpackVectorNode() {
        super("unpack_vector", Material.IRON_INGOT);

        Input<Vec> vector = new Input<>("vector", VectorType.INSTANCE);
        Output<Double> x = new Output<>("x", NumberType.INSTANCE);
        Output<Double> y = new Output<>("y", NumberType.INSTANCE);
        Output<Double> z = new Output<>("z", NumberType.INSTANCE);

        x.valueFrom(ctx -> vector.getValue(ctx).x());
        y.valueFrom(ctx -> vector.getValue(ctx).y());
        z.valueFrom(ctx -> vector.getValue(ctx).z());
    }

    @Override
    public Node copy() {
        return new UnpackVectorNode();
    }
}
