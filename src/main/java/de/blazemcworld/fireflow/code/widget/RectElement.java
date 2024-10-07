package de.blazemcworld.fireflow.code.widget;

import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;

public class RectElement {

    public Vec pos = Vec.ZERO;
    public Vec size = Vec.ZERO;
    private final LineElement top = new LineElement();
    private final LineElement bottom = new LineElement();
    private final LineElement left = new LineElement();
    private final LineElement right = new LineElement();

    public void update(InstanceContainer inst) {
        top.from = pos;
        top.to = pos.add(-size.x(), 0, 0);

        bottom.from = pos.add(0, -size.y(), 0);
        bottom.to = pos.add(-size.x(), -size.y(), 0);

        left.from = pos;
        left.to = pos.add(0, -size.y(), 0);

        right.from = pos.add(-size.x(), 0, 0);
        right.to = pos.add(-size.x(), -size.y(), 0);

        top.update(inst);
        bottom.update(inst);
        left.update(inst);
        right.update(inst);
    }

    public void remove() {
        top.remove();
        bottom.remove();
        left.remove();
        right.remove();
    }

    public void color(TextColor color) {
        top.color(color);
        bottom.color(color);
        left.color(color);
        right.color(color);
    }
}
