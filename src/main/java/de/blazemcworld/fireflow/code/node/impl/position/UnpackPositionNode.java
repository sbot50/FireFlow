package de.blazemcworld.fireflow.code.node.impl.position;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.PositionType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.item.Material;

public class UnpackPositionNode extends Node {

    public UnpackPositionNode() {
        super("unpack_position", Material.GOLD_INGOT);

        Input<Pos> position = new Input<>("position", PositionType.INSTANCE);
        Output<Double> x = new Output<>("x", NumberType.INSTANCE);
        Output<Double> y = new Output<>("y", NumberType.INSTANCE);
        Output<Double> z = new Output<>("z", NumberType.INSTANCE);
        Output<Double> pitch = new Output<>("pitch", NumberType.INSTANCE);
        Output<Double> yaw = new Output<>("yaw", NumberType.INSTANCE);

        x.valueFrom(ctx -> position.getValue(ctx).x());
        y.valueFrom(ctx -> position.getValue(ctx).y());
        z.valueFrom(ctx -> position.getValue(ctx).z());
        pitch.valueFrom(ctx -> (double) position.getValue(ctx).pitch());
        yaw.valueFrom(ctx -> (double) position.getValue(ctx).yaw());
    }

    @Override
    public Node copy() {
        return new UnpackPositionNode();
    }
}
