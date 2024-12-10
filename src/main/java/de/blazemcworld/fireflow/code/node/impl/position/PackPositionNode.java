package de.blazemcworld.fireflow.code.node.impl.position;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.PositionType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.item.Material;

public class PackPositionNode extends Node {

    public PackPositionNode() {
        super("pack_position", Material.GOLD_BLOCK);

        Input<Double> x = new Input<>("x", NumberType.INSTANCE);
        Input<Double> y = new Input<>("y", NumberType.INSTANCE);
        Input<Double> z = new Input<>("z", NumberType.INSTANCE);
        Input<Double> pitch = new Input<>("pitch", NumberType.INSTANCE);
        Input<Double> yaw = new Input<>("yaw", NumberType.INSTANCE);
        Output<Pos> position = new Output<>("position", PositionType.INSTANCE);

        position.valueFrom(ctx -> new Pos(
                x.getValue(ctx),
                y.getValue(ctx),
                z.getValue(ctx),
                pitch.getValue(ctx).floatValue(),
                yaw.getValue(ctx).floatValue()
        ));
    }

    @Override
    public PackPositionNode copy() {
        return new PackPositionNode();
    }

}
