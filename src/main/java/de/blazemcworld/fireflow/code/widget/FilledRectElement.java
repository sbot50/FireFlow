package de.blazemcworld.fireflow.code.widget;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.InstanceContainer;

public class FilledRectElement {
    
    public Vec pos = Vec.ZERO;
    public Vec size = Vec.ZERO;
    private final Entity display = new Entity(EntityType.TEXT_DISPLAY);
    private final TextDisplayMeta meta = (TextDisplayMeta) display.getEntityMeta();

    public FilledRectElement(int argb) {
        meta.setText(Component.text(" "));
        meta.setBackgroundColor(argb);
        meta.setLineWidth(Integer.MAX_VALUE);
        meta.setTransformationInterpolationDuration(1);
        meta.setPosRotInterpolationDuration(1);
        meta.setHasNoGravity(true);
    }

    public void update(InstanceContainer inst) {
        Vec adjustedSize = new Vec(size.x() * 8, size.y() * 4, 1);
        meta.setScale(adjustedSize);
        Vec adjustedPos = new Vec(
            pos.x() - size.x() / 2.5,
            pos.y() - size.y(),
            15.9995
        );
        display.setInstance(inst, adjustedPos.asPosition().withView(180f, 0f));
    }

    public void remove() {
        display.remove();
    }

    public void color(int argb) {
        meta.setBackgroundColor(argb);
    }
}