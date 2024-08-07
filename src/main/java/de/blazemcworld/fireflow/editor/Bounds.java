package de.blazemcworld.fireflow.editor;

import net.minestom.server.coordinate.Vec;

public class Bounds {

    public final Vec min;
    public final Vec max;

    public Bounds(Vec a, Vec b) {
        min = a.min(b);
        max = a.max(b);
    }

    public Vec size() {
        return Vec.fromPoint(max).sub(min);
    }

    public boolean includes2d(Vec cursor) {
        return min.x() < cursor.x() && min.y() < cursor.y()
                && max.x() > cursor.x() && max.y() > cursor.y();
    }
}
