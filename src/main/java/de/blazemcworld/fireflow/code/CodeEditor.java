package de.blazemcworld.fireflow.code;

import de.blazemcworld.fireflow.code.action.Action;
import de.blazemcworld.fireflow.code.node.NodeList;
import de.blazemcworld.fireflow.code.widget.NodeMenuWidget;
import de.blazemcworld.fireflow.code.widget.Widget;
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
import net.minestom.server.event.player.PlayerSwapItemEvent;
import net.minestom.server.event.player.PlayerTickEvent;
import net.minestom.server.event.trait.InstanceEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CodeEditor {

    public final Space space;
    public final Set<Widget> rootWidgets = new HashSet<>();
    public final HashMap<Player, Set<Widget>> lockedWidgets = new HashMap<>();
    private final HashMap<Player, Action> actions = new HashMap<>();

    public CodeEditor(Space space) {
        this.space = space;

        EventNode<InstanceEvent> events = space.code.eventNode();

        events.addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();

            player.setAllowFlying(true);
            player.setFlying(true);

            Entity interactionHelper = new Entity(EntityType.INTERACTION);
            interactionHelper.setNoGravity(true);
            InteractionMeta meta = (InteractionMeta) interactionHelper.getEntityMeta();
            meta.setWidth(-0.5f);
            meta.setHeight(-0.5f);
            interactionHelper.setInstance(event.getInstance(), Pos.ZERO);
            player.addPassenger(interactionHelper);

            lockedWidgets.put(player, new HashSet<>());
        });

        events.addListener(PlayerExitInstanceEvent.class, event -> {
            for (Entity passenger : event.getEntity().getPassengers()) {
                if (passenger.getEntityType() == EntityType.INTERACTION) passenger.remove();
            }

            if (actions.containsKey(event.getPlayer())) actions.get(event.getPlayer()).stop(this, event.getPlayer());
            actions.remove(event.getPlayer());
            lockedWidgets.remove(event.getPlayer());
        });

        events.addListener(PlayerEntityInteractEvent.class, event -> {
            handleInteraction(event.getPlayer(), Interaction.Type.RIGHT_CLICK);
        });
        events.addListener(PlayerSwapItemEvent.class, event -> {
            handleInteraction(event.getPlayer(), Interaction.Type.SWAP_HANDS);
        });
        events.addListener(EntityAttackEvent.class, event -> {
            if (event.getEntity() instanceof Player player) {
                handleInteraction(player, Interaction.Type.LEFT_CLICK);
            }
        });

        events.addListener(PlayerTickEvent.class, event -> {
            Action a = actions.get(event.getPlayer());
            if (a == null) return;
            Vec cursor = getCursor(event.getPlayer());
            cursor = cursor.withX(Math.round(cursor.x() * 8) / 8f)
                    .withY(Math.round(cursor.y() * 8) / 8f);
            a.tick(cursor, this, event.getPlayer());
        });
    }

    private void handleInteraction(Player player, Interaction.Type type) {
        Vec pos = getCursor(player).mul(8).apply(Vec.Operator.CEIL).div(8).withZ(15.999);
        Interaction i = new Interaction(this, player, pos, type);

        if (actions.containsKey(player)) {
            actions.get(player).interact(i);
            return;
        }

        for (Widget w : new HashSet<>(rootWidgets)) {
            if (w.interact(i)) return;
        }

        if (type == Interaction.Type.RIGHT_CLICK) {
            NodeMenuWidget n = new NodeMenuWidget(NodeList.nodes);
            Vec s = n.getSize();
            n.setPos(pos.add(Math.round(s.x() * 4) / 8f, Math.round(s.y() * 4) / 8f, 0));
            n.update(space.code);
            rootWidgets.add(n);
        }
    }

    private Vec getCursor(Player player) {
        double norm = player.getPosition().direction().dot(new Vec(0, 0, -1));
        if (norm >= 0) return Vec.ZERO.withZ(15.999);
        Vec start = player.getPosition().asVec().add(0.0, player.getEyeHeight(), -16);
        double dist = -start.dot(new Vec(0, 0, -1)) / norm;
        if (dist < 0) return Vec.ZERO.withZ(15.999);
        Vec out = start.add(player.getPosition().direction().mul(dist)).withZ(15.999);
        if (out.y() > 99999) out = out.withY(99999);
        if (out.y() < -99999) out = out.withY(-99999);
        if (out.x() > 99999) out = out.withX(99999);
        if (out.x() < -99999) out = out.withX(-99999);
        return out;
    }

    public void unlockWidget(Widget widget, Player player) {
        lockedWidgets.computeIfAbsent(player, p -> new HashSet<>()).remove(widget);
    }

    public void unlockWidgets(Player player) {
        lockedWidgets.remove(player);
    }

    public boolean lockWidget(Widget widget, Player player) {
        for (Map.Entry<Player, Set<Widget>> entry : lockedWidgets.entrySet()) {
            if (entry.getValue().contains(widget)) return entry.getKey() == player;
        }
        lockedWidgets.computeIfAbsent(player, p -> new HashSet<>()).add(widget);
        return true;
    }

    public void setAction(Player player, Action action) {
        if (actions.containsKey(player)) {
            actions.get(player).stop(this, player);
        }
        actions.put(player, action);
    }

    public void stopAction(Player player) {
        if (actions.containsKey(player)) {
            actions.get(player).stop(this, player);
        }
        actions.remove(player);
    }
}
