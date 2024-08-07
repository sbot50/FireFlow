package de.blazemcworld.fireflow.editor;

import de.blazemcworld.fireflow.editor.widget.NodeCategoryWidget;
import de.blazemcworld.fireflow.node.NodeCategory;
import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.util.PlayerExitInstanceEvent;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.InstanceContainer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CodeEditor {

    private final InstanceContainer inst;
    public final List<Widget> widgets = new ArrayList<>();

    public CodeEditor(Space space) {
        inst = space.code;

        EventNode<InstanceEvent> events = inst.eventNode();

        events.addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();
            player.setAllowFlying(true);
            player.setFlying(true);

            Entity helper = new Entity(EntityType.INTERACTION);
            helper.setNoGravity(true);
            InteractionMeta meta = (InteractionMeta) helper.getEntityMeta();
            meta.setWidth(-0.5f);
            meta.setHeight(-0.5f);
            helper.setInstance(inst, Pos.ZERO);
            player.addPassenger(helper);
        });

        events.addListener(PlayerExitInstanceEvent.class, event -> {
            for (Entity passenger : event.getPlayer().getPassengers()) {
                if (passenger.getEntityType() == EntityType.INTERACTION) {
                    passenger.remove();
                }
            }
        });

        events.addListener(PlayerEntityInteractEvent.class, event -> {
            Vec cursor = getCursor(event.getPlayer());
            Widget selected = getWidget(event.getPlayer(), cursor);
            if (selected == null) {
                widgets.add(new NodeCategoryWidget(cursor, inst, NodeCategory.ROOT));
                return;
            }
            selected.rightClick(cursor, event.getPlayer(), this);
        });

        events.addListener(EntityAttackEvent.class, event -> {
            if (event.getEntity() instanceof Player player) {
                Vec cursor = getCursor(player);
                Widget selected = getWidget(player, cursor);
                if (selected == null) return;
                selected.leftClick(cursor, player, this);
            }
        });
    }

    private @Nullable Widget getWidget(Player player, Vec cursor) {
        for (Widget w : widgets) {
            Widget res = w.select(player, cursor);
            if (res != null) return res;
        }
        return null;
    }

    private Vec getCursor(Player player) {
        double norm = player.getPosition().direction().dot(new Vec(0, 0, -1));
        if (norm >= 0) return Vec.ZERO.withZ(15.999);
        Vec start = player.getPosition().asVec().add(0.0, player.getEyeHeight(), -16);
        double dist = -start.dot(new Vec(0, 0, -1)) / norm;
        if (dist < 0) return Vec.ZERO.withZ(15.999);
        return start.add(player.getPosition().direction().mul(dist)).withZ(15.999);
    }


    public void remove(Widget widget) {
        widget.remove();
        widgets.remove(widget);
    }
}
