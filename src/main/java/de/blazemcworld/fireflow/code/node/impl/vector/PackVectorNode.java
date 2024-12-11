package de.blazemcworld.fireflow.code.node.impl.vector;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.VectorType;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.Material;

public class PackVectorNode extends Node {

    public PackVectorNode() {
        super("pack_vector", Material.IRON_BLOCK);

        Input<Double> x = new Input<>("x", NumberType.INSTANCE);
        Input<Double> y = new Input<>("y", NumberType.INSTANCE);
        Input<Double> z = new Input<>("z", NumberType.INSTANCE);
        Output<Vec> vector = new Output<>("vector", VectorType.INSTANCE);

        vector.valueFrom(ctx -> new Vec(
                x.getValue(ctx),
                y.getValue(ctx),
                z.getValue(ctx)
        ));
    }

    @Override
    public Node copy() {
        return new PackVectorNode();
    }

}
