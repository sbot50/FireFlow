package de.blazemcworld.fireflow.code.widget;

import java.util.List;

import de.blazemcworld.fireflow.code.Interaction;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;

public class BorderWidget<T extends Widget> implements Widget {

    public final T inner;
    private Vec pos = Vec.ZERO;
    private final RectElement rect = new RectElement();
    public double padding = 1/8f;
    public double margin = 0f;
    private FilledRectElement background = null;

    public BorderWidget(T inner) {
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

        if (background != null) {
            background.pos = rect.pos;
            background.size = rect.size;
            background.update(inst);
        }
    }

    @Override
    public void remove() {
        rect.remove();
        inner.remove();
        if (background != null) {
            background.remove();
        }
    }

    @Override
    public boolean interact(Interaction i) {
        return inner.interact(i);
    }

    public void color(TextColor color) {
        rect.color(color);
    }

    @Override
    public Widget getWidget(Vec pos) {
        if (!inBounds(pos)) return null;
        if (inner.getWidget(pos) == null) return this;
        return inner.getWidget(pos);
    }

    @Override
    public List<Widget> getChildren() {
        return List.of(inner);
    }

    public void backgroundColor(int argb) {
        if (argb == 0) {
            if (background != null) {
                background.remove();
                background = null;
            }
            return;
        }
        if (background == null) {
            background = new FilledRectElement(argb);
            background.pos = getPos();
            background.size = getSize();
        } else {
            background.color(argb);
        }
    }
}
