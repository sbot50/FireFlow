package de.blazemcworld.fireflow.code.node.impl.vector;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.StringType;
import de.blazemcworld.fireflow.code.type.VectorType;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.Material;

public class GetVectorComponentNode extends Node {
    public GetVectorComponentNode() {
        super("get_vector_component", Material.DARK_PRISMARINE);
        Input<Vec> vector = new Input<>("vector", VectorType.INSTANCE);
        Input<String> axis = new Input<>("axis", StringType.INSTANCE).options("X", "Y", "Z");
        Output<Double> output = new Output<>("output", NumberType.INSTANCE);

        output.valueFrom((ctx -> {
            Vec inputVector = vector.getValue(ctx);
            return switch (axis.getValue(ctx)) {
                case "X" -> inputVector.x();
                case "Y" -> inputVector.y();
                case "Z" -> inputVector.z();
                default -> null;
            };
        }));
    }

    @Override
    public Node copy() {
        return new GetVectorComponentNode();
    }
}