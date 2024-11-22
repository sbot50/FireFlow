package de.blazemcworld.fireflow.code.widget;

import java.util.List;

import de.blazemcworld.fireflow.code.Interaction;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta.DisplayContext;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class ItemWidget implements Widget {
    
    private final Entity display = new Entity(EntityType.ITEM_DISPLAY);
    private final ItemDisplayMeta meta = (ItemDisplayMeta) display.getEntityMeta();
    private Vec pos = Vec.ZERO;    

    public ItemWidget(ItemStack stack, double size) {
        meta.setItemStack(stack);
        meta.setTransformationInterpolationDuration(1);
        meta.setPosRotInterpolationDuration(1);
        meta.setHasNoGravity(true);
        meta.setDisplayContext(DisplayContext.GUI);
        meta.setScale(new Vec(size, size, 0.001));
        meta.setTranslation(new Vec(-size / 2, -size / 2, 0));
    }

    public ItemWidget(Material type) {
        this(ItemStack.of(type), 0.25);
    }

    public void setItem(ItemStack stack) {
        meta.setItemStack(stack);
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
        return new Vec(0.25, 0.25, 0);
    }

    @Override
    public void update(InstanceContainer inst) {
        display.setInstance(inst, pos.asPosition());
    }

    @Override
    public void remove() {
        display.remove();
    }

    @Override
    public List<Widget> getChildren() {
        return null;
    }

    @Override
    public boolean interact(Interaction i) {
        return false;
    }
}
