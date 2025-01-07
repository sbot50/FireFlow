package de.blazemcworld.fireflow.code.node.impl.vector;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.StringType;
import de.blazemcworld.fireflow.code.type.VectorType;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.Material;

public class SetVectorComponentNode extends Node {
    public SetVectorComponentNode() {
        super("set_vector_component", Material.PRISMARINE_SHARD);
        Input<Vec> vector = new Input<>("vector", VectorType.INSTANCE);
        Input<String> axis = new Input<>("axis", StringType.INSTANCE).options("X", "Y", "Z");
        Input<Double> value = new Input<>("value", NumberType.INSTANCE);
        Output<Vec> output = new Output<>("output", VectorType.INSTANCE);

        output.valueFrom((ctx) -> {
            Vec outputVec = vector.getValue(ctx);
            double outputValue = value.getValue(ctx);
            return switch (axis.getValue(ctx)) {
                case "X" -> new Vec(outputValue, outputVec.y(), outputVec.z());
                case "Y" -> new Vec(outputVec.x(), outputValue, outputVec.z());
                case "Z" -> new Vec(outputVec.x(), outputVec.y(), outputValue);
                default -> null;
            };
        });
    }


    @Override
    public Node copy() {
        return new SetVectorComponentNode();
    }
}