package de.blazemcworld.fireflow.code.widget;

import de.blazemcworld.fireflow.code.Interaction;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;

public class BorderWidget implements Widget {

    private Vec pos = Vec.ZERO;
    private final Widget inner;
    private final RectElement rect = new RectElement();
    public double padding = 1/8f;
    public double margin = 0f;

    public BorderWidget(Widget inner) {
        this.inner = inner;
    }

    @Override
    public void setPos(Vec pos) {
        this.pos = pos;
    }

    @Override
    public Vec getPos() {
        return pos;
    }

    @Override
    public Vec getSize() {
        return inner.getSize().add((padding + margin) * 2, (padding + margin) * 2, 0);
    }

    @Override
    public void update(InstanceContainer inst) {
        Vec current = pos.add(-margin, -margin, 0);

        rect.pos = current;
        rect.size = inner.getSize().add(padding * 2, padding * 2, 0);
        rect.update(inst);

        current = current.add(-padding, -padding, 0);
        inner.setPos(current);
        inner.update(inst);
    }

    @Override
    public void remove() {
        rect.remove();
        inner.remove();
    }

    @Override
    public boolean interact(Interaction i) {
        return inner.interact(i);
    }

    public void color(TextColor color) {
        rect.color(color);
    }
}
