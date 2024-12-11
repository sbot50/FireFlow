package de.blazemcworld.fireflow.code.node.impl.position;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.PositionType;
import de.blazemcworld.fireflow.code.type.VectorType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.Material;

public class FacingVectorNode extends Node {

    public FacingVectorNode() {
        super("facing_vector", Material.ENDER_EYE);

        Input<Pos> position = new Input<>("position", PositionType.INSTANCE);
        Output<Vec> vector = new Output<>("vector", VectorType.INSTANCE);

        vector.valueFrom(ctx -> position.getValue(ctx).direction());
    }

    @Override
    public Node copy() {
        return new FacingVectorNode();
    }
}
