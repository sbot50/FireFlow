package de.blazemcworld.fireflow.code.widget;

import de.blazemcworld.fireflow.code.Interaction;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;

public class SpacingWidget implements Widget {

    private Vec pos = Vec.ZERO;
    public Vec size;

    public SpacingWidget(Vec size) {
        this.size = size;
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
        return size;
    }

    @Override
    public void update(InstanceContainer inst) {
    }

    @Override
    public void remove() {
    }

    @Override
    public boolean interact(Interaction i) {
        return false;
    }
}
